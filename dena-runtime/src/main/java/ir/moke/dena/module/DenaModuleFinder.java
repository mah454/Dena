package ir.moke.dena.module;

import ir.moke.dena.utils.FileUtils;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DenaModuleFinder implements ModuleFinder {
    private final Set<String> excludedModules;
    private final ModuleFinder delegateFinder;
    private final List<Configuration> parentConfigurations;
    private final List<ModuleLayer> parentLayers;
    private final Set<ModuleReference> allModulesCache;

    public DenaModuleFinder(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!FileUtils.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory: " + path);
        }

        this.delegateFinder = ModuleFinder.of(path);
        Set<ModuleReference> foundModules = this.delegateFinder.findAll();

        // Parse module-info of modules in this path to find their requirements
        Set<String> requiredModuleNames = foundModules.stream()
                .flatMap(ref -> ref.descriptor().requires().stream())
                .map(ModuleDescriptor.Requires::name)
                .filter(name -> !name.startsWith("java.") && !name.startsWith("jdk."))
                .collect(Collectors.toSet());

        // Start with Boot Layer
        ModuleLayer bootLayer = ModuleLayer.boot();
        Set<ModuleLayer> parentLayerSet = new LinkedHashSet<>();
        parentLayerSet.add(bootLayer);

        // Look up required modules in ModuleRepository and add their layers as parents
        for (String requiredName : requiredModuleNames) {
            if (ModuleRepository.isExists(requiredName)) {
                ModuleContext context = ModuleRepository.get(requiredName);
                if (context != null && context.getLayer() != null) {
                    ModuleLayer dependencyLayer = context.getLayer();
                    // Add all parent layers of the dependency to avoid resolution gaps
                    // This ensures transitive visibility
                    parentLayerSet.addAll(dependencyLayer.parents());
                    parentLayerSet.add(dependencyLayer);
                }
            }
        }

        this.parentLayers = List.copyOf(parentLayerSet);
        this.parentConfigurations = parentLayers.stream()
                .map(ModuleLayer::configuration)
                .collect(Collectors.toList());

        // Exclude modules already loaded in parent layers (including boot + dependencies)
        Set<String> exclusions = parentLayers.stream()
                .flatMap(layer -> layer.modules().stream())
                .map(Module::getName)
                .collect(Collectors.toSet());

        this.allModulesCache = foundModules.stream()
                .filter(ref -> !exclusions.contains(ref.descriptor().name()))
                .collect(Collectors.toUnmodifiableSet());

        this.excludedModules = Set.copyOf(exclusions);
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        if (excludedModules.contains(name)) {
            return Optional.empty(); // Resolve from parent layer
        }
        return delegateFinder.find(name);
    }

    @Override
    public Set<ModuleReference> findAll() {
        return allModulesCache;
    }

    public Set<String> getExcludedModules() {
        return excludedModules;
    }

    public List<ModuleLayer> getParentLayers() {
        return parentLayers;
    }

    public List<Configuration> getParentConfigurations() {
        return parentConfigurations;
    }
}
