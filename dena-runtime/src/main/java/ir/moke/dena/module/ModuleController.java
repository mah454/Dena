package ir.moke.dena.module;

import ir.moke.dena.GlobalVariables;
import ir.moke.dena.api.IModule;
import ir.moke.dena.api.ModuleMetadata;
import ir.moke.dena.utils.FileSystemUtils;
import ir.moke.dena.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ModuleController implements GlobalVariables {
    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);
    private static final ExecutorService controllerExecutorService = Executors.newSingleThreadExecutor();

    static {
        FileUtils.createDirectory(denaModulesDirectory);
        listenWorkDir();
    }

    private static void listenWorkDir() {
        List<WatchEvent.Kind<?>> kindList = List.of(StandardWatchEventKinds.ENTRY_DELETE);
        controllerExecutorService.submit(() -> FileSystemUtils.watchPath(denaModulesDirectory, kindList, ModuleController::onDeleteModule));
    }

    @SuppressWarnings("unchecked")
    private static void onDeleteModule(WatchEvent<?> event) {
        WatchEvent<Path> ev = (WatchEvent<Path>) event;
        Path moduleDirPath = ev.context();
        String moduleName = moduleDirPath.getFileName().toString();
        listOnUsedModules(moduleName).stream().map(ModuleContext::getName).forEach(ModuleController::stop);
        stop(moduleName);
        deactivateModule(ModuleRepository.get(moduleName));
    }

    public static void initStartUp() {
        List<Path> list = FileUtils.listFiles(denaModulesDirectory);
        for (Path item : list) {
            String moduleName = item.getFileName().toString();
            loadDependencyModule(item);
            load(moduleName);
        }
    }

    public static void load(String moduleName) {
        try {
            if (ModuleRepository.isExists(moduleName)) return;
            Path modulePath = denaModulesDirectory.resolve(moduleName);
            if (!FileUtils.isFileExists(modulePath)) {
                throw new IllegalStateException("Module %s directory not exists".formatted(moduleName));
            }
            URLClassLoader classLoader = new URLClassLoader(new URL[]{modulePath.toUri().toURL()});
            loadDependencyModule(modulePath);
            ModuleLayer moduleLayer = createLayer(modulePath, classLoader);

            // module
            Path moduleJarPath = getIModuleJarFile(moduleLayer, moduleName);
            if (moduleJarPath == null || !FileUtils.isFileExists(moduleJarPath)) {
                throw new IllegalStateException("ModuleLayer not contain module with name %s".formatted(moduleName));
            }
            Module module = moduleLayer.modules()
                    .stream()
                    .filter(item -> item.getName().equals(moduleName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Module does not exists"));

            // detect metadata
            ModuleMetadata metadata = module.getDeclaredAnnotation(ModuleMetadata.class);
            String description = metadata != null ? metadata.description() : null;
            String maintainer = metadata != null ? metadata.maintainer() : null;
            String url = metadata != null ? metadata.url() : null;
            Optional<ModuleDescriptor.Version> optionalVersion = module.getDescriptor().version();

            ModuleContext context = new ModuleContext(moduleLayer, modulePath, moduleName, description, maintainer, url);
            IModule iModule = detectIModule(context);
            context.setIModule(iModule);
            context.setClassLoader(classLoader);
            optionalVersion.ifPresent(item -> context.setVersion(item.toString()));
            ModuleRepository.add(context);
            logger.info("[{}] - Module loaded", moduleName);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public static void unload(String moduleName) {
        try {
            ModuleContext context = ModuleRepository.get(moduleName);
            unloadDependentModule(context);
            stop(moduleName);
            deactivateModule(context);
            ModuleRepository.remove(moduleName);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        } finally {
            triggerGC();
        }
    }

    private static ModuleLayer createLayer(Path path, ClassLoader classLoader) {
        try {
            String targetModuleName = getModuleName(path);

            DenaModuleFinder moduleFinder = new DenaModuleFinder(path);
            List<ModuleLayer> parentLayers = moduleFinder.getParentLayers();
            List<Configuration> parentConfigurations = moduleFinder.getParentConfigurations();

            Configuration configuration = Configuration.resolveAndBind(
                    moduleFinder,
                    parentConfigurations,
                    ModuleFinder.of(),
                    List.of(targetModuleName)
            );

            logger.info("[{}] - Create module layer", targetModuleName);
            return ModuleLayer.defineModulesWithOneLoader(configuration, parentLayers, classLoader).layer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static IModule detectIModule(ModuleContext moduleContext) {
        ModuleLayer layer = moduleContext.getLayer();

        // Get modules that are actually IN this layer (not parents)
        Set<String> currentLayerModuleNames = layer.modules()
                .stream()
                .map(Module::getName)
                .collect(Collectors.toSet());

        // Filter providers to only those from the current layer
        List<ServiceLoader.Provider<IModule>> providers = ServiceLoader
                .load(layer, IModule.class)
                .stream()
                .filter(provider -> {
                    String providerModuleName = provider.type().getModule().getName();
                    return currentLayerModuleNames.contains(providerModuleName);
                })
                .toList();

//        if (providers.isEmpty()) {
//            throw new RuntimeException("Module %s does not implement IModule interface".formatted(moduleContext.getName()));
//        }

        if (providers.isEmpty()) return null;

        if (providers.size() > 1) {
            String implementations = providers.stream()
                    .map(p -> p.type().getName() + " (" + p.type().getModule().getName() + ")")
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("More than one IModule implementation found in module %s: %s".formatted(moduleContext.getName(), implementations));
        }

        return providers.getFirst().get();
    }

    public static void start(String moduleName) {
        ModuleContext context = ModuleRepository.get(moduleName);
        if (context == null) throw new IllegalStateException("Module %s do not loaded yet".formatted(moduleName));
        IModule iModule = context.getIModule();

        if (iModule != null && !context.isRunning()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            logger.info("[{}] - Start module", moduleName);
            executorService.submit(() -> {
                try {
                    iModule.start();
                    context.setRunning(true);
                } catch (Exception e) {
                    logger.error("Module error", e);
                    context.setExecutorService(null);
                    context.setRunning(false);
                    executorService.shutdown();
                }
            });

            // set context executor service
            context.setExecutorService(executorService);
        } else {
            throw new IllegalStateException("Module %s does not implemented IModule interface".formatted(moduleName));
        }
    }

    public static void stop(String moduleName) {
        ModuleContext context = ModuleRepository.get(moduleName);
        if (context == null) throw new IllegalStateException("Module %s does not exists".formatted(moduleName));
        if (!context.isRunning()) return;
        IModule iModule = context.getIModule();
        // Try to unload module
        try (ExecutorService es = context.getExecutorService()) {

            logger.info("[{}] - Stop module", moduleName);
            iModule.stop();

            logger.info("[{}] - Shutdown module executor service", moduleName);
            es.shutdown();

            logger.info("[{}] - CleaUp module", moduleName);
            context.setRunning(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            triggerGC();
        }
    }

    private static Path getIModuleJarFile(ModuleLayer moduleLayer, String moduleName) {
        return moduleLayer.configuration()
                .findModule(moduleName)
                .map(ResolvedModule::reference)
                .flatMap(ModuleReference::location)
                .map(URI::getPath)
                .map(Path::of)
                .orElse(null);
    }

    private static String getModuleName(Path path) {
        return path.getName(path.getNameCount() - 1).toString();
    }

    private static void deactivateModule(ModuleContext context) {
        context.setIModule(null);
        context.setClassLoader(null);
        context.setExecutorService(null);
        context.setLayer(null);
        ModuleRepository.remove(context);
    }

    private static List<ModuleContext> listOnUsedModules(String moduleName) {
        List<ModuleContext> list = new ArrayList<>();
        for (ModuleContext context : ModuleRepository.list()) {
            for (ModuleLayer parent : context.getLayer().parents()) {
                parent.modules()
                        .stream()
                        .map(Module::getName)
                        .filter(item -> item.equals(moduleName))
                        .findFirst()
                        .ifPresent(_ -> list.add(context));
            }
        }

        return list;
    }

    private static void loadDependencyModule(Path item) {
        try {
            ModuleFinder moduleFinder = ModuleFinder.of(item);
            for (ModuleReference moduleReference : moduleFinder.findAll()) {
                for (ModuleDescriptor.Requires req : moduleReference.descriptor().requires()) {
                    boolean exists = FileUtils.isFileExists(denaModulesDirectory.resolve(req.name()));
                    if (exists) {
                        load(req.name());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private static void unloadDependentModule(ModuleContext context) {
        ModuleRepository.findDependentModules(context)
                .stream()
                .map(ModuleContext::getName)
                .forEach(ModuleController::unload);
    }

    private static boolean isModuleExists(String moduleName) {
        return FileUtils.isFileExists(denaModulesDirectory.resolve(moduleName));
    }

    private static void triggerGC() {
        logger.info("Trigger GC");
        System.gc();
    }
}
