package ir.moke.dena.module;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DenaModuleFinder implements ModuleFinder {
    private final Path path;
    private final Set<String> excludedModules = new HashSet<>();
    private final ModuleFinder moduleFinder;
    private final List<Configuration> parentConfigurations;
    private List<ModuleLayer> parentLayers = new ArrayList<>();

    public DenaModuleFinder(Path path) {
        this.path = path;
        this.moduleFinder = ModuleFinder.of(path);

        List<ModuleDescriptor> descriptors = moduleFinder.findAll().stream()
                .map(ModuleReference::descriptor)
                .toList();

        for (ModuleDescriptor descriptor : descriptors) {
            for (ModuleDescriptor.Requires require : descriptor.requires()) {
                ModuleContext depContext = ModuleRepository.get(require.name());
                if (depContext != null) {
                    ModuleLayer depLayer = depContext.getLayer();
                    parentLayers.add(depLayer);
                    depLayer.modules().forEach(m -> excludedModules.add(m.getName()));
                }
            }
        }

        if (parentLayers.isEmpty()) {
            parentLayers = List.of(ModuleLayer.boot());
            ModuleLayer.boot().modules().forEach(m -> excludedModules.add(m.getName()));
        }

        parentConfigurations = parentLayers.stream().map(ModuleLayer::configuration).toList();
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        if (excludedModules.contains(name)) {
            return Optional.empty(); // Pretend it doesn't exist here
        }

        ModuleFinder moduleFinder = ModuleFinder.of(path);
        return moduleFinder.find(name);
    }

    @Override
    public Set<ModuleReference> findAll() {
        return moduleFinder.findAll().stream()
                .filter(ref -> !excludedModules.contains(ref.descriptor().name()))
                .collect(Collectors.toSet());
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
