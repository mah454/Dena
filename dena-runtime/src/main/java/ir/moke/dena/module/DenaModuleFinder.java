package ir.moke.dena.module;

import ir.moke.dena.GlobalVariables;
import ir.moke.utils.FileUtils;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DenaModuleFinder implements ModuleFinder, GlobalVariables {
    private final Set<String> excludedModules;
    private final ModuleFinder delegateFinder;
    private final List<Configuration> parentConfigurations;
    private final List<ModuleLayer> parentLayers;
    private final Set<ModuleReference> allModulesCache;

    static {
        try {
            FileUtils.createDirectory(denaSharedDirectory);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to create shared directory: " + e.getMessage());
        }
    }

    public DenaModuleFinder(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!FileUtils.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory: " + path);
        }

        this.delegateFinder = ModuleFinder.of(path);

        // Build shared layer first
        ModuleLayer sharedLayer = createSharedLayer();

        // Track modules already available in parent layers to avoid duplicates
        Set<String> availableInParents = new HashSet<>();
        sharedLayer.modules().forEach(m -> availableInParents.add(m.getName()));

        List<ModuleLayer> layers = new ArrayList<>();
//        layers.add(ModuleLayer.boot());
        layers.add(sharedLayer);

        Set<String> exclusions = new HashSet<>(availableInParents);

        // Discover dependencies and build parent layers
        Set<ModuleReference> allModules = delegateFinder.findAll();

        // Filter out excluded modules from the cache
        this.allModulesCache = allModules.stream()
                .filter(ref -> !exclusions.contains(ref.descriptor().name()))
                .collect(Collectors.toUnmodifiableSet());

        for (ModuleReference ref : allModules) {
            ModuleDescriptor descriptor = ref.descriptor();
            for (ModuleDescriptor.Requires require : descriptor.requires()) {
                String depName = require.name();
                ModuleContext depContext = ModuleRepository.get(depName);

                if (depContext != null) {
                    ModuleLayer depLayer = depContext.getLayer();

                    // CRITICAL FIX: Only add dependency layer if it provides
                    // modules not already available in previous layers
                    Set<String> depModuleNames = depLayer.modules().stream()
                            .map(Module::getName)
                            .collect(Collectors.toSet());

                    boolean providesNewModules = depModuleNames.stream()
                            .anyMatch(name -> !availableInParents.contains(name));

                    if (providesNewModules && !layers.contains(depLayer)) {
                        layers.add(depLayer);
                        availableInParents.addAll(depModuleNames);
                        exclusions.addAll(depModuleNames);
                    }
                }
            }
        }

        this.parentLayers = List.copyOf(layers);
        this.excludedModules = Set.copyOf(exclusions);

        // Build configurations from parent layers
        this.parentConfigurations = parentLayers.stream()
                .map(ModuleLayer::configuration)
                .distinct()
                .toList();
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        if (excludedModules.contains(name)) {
            return Optional.empty();
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

    private ModuleLayer createSharedLayer() {
        ModuleFinder finder = ModuleFinder.of(denaSharedDirectory);

        List<String> moduleNames = finder.findAll().stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .toList();

        if (moduleNames.isEmpty()) {
            return ModuleLayer.boot();
        }

        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration configuration = Configuration.resolveAndBind(
                finder,
                List.of(bootLayer.configuration()),
                ModuleFinder.of(),
                moduleNames
        );

        return ModuleLayer.defineModulesWithOneLoader(
                configuration,
                List.of(bootLayer),
                ClassLoader.getSystemClassLoader()
        ).layer();
    }
}
