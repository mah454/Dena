package ir.moke.dena.module;

import ir.moke.dena.GlobalVariables;
import ir.moke.dena.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleRepository implements GlobalVariables {
    private static final Logger logger = LoggerFactory.getLogger(ModuleRepository.class);
    private static final List<ModuleContext> MODULES = new ArrayList<>();

    public static void add(ModuleContext moduleContext) {
        remove(moduleContext);
        MODULES.add(moduleContext);
        logger.info("[{}] - Module added to repository", moduleContext.getName());
    }

    public static boolean isExists(String name) {
        return MODULES.stream().anyMatch(item -> item.getName().equals(name));
    }

    public static ModuleContext get(String name) {
        return MODULES.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Set<ModuleContext> list() {
        // list all directories could be modules
        Set<ModuleContext> list = new HashSet<>(FileUtils.listFiles(denaModulesDirectory)
                .stream()
                .filter(Files::isDirectory)
                .map(item -> new ModuleContext(item.getFileName().toString(), item))
                .toList());

        list.removeAll(MODULES);
        list.addAll(MODULES);
        return list;
    }

    public static void remove(String name) {
        ModuleContext moduleContext = get(name);
        remove(moduleContext);
    }

    public static void remove(ModuleContext context) {
        MODULES.remove(context);
    }

    public static List<ModuleContext> findDependentModules(ModuleContext context) {
        return MODULES.stream()
                .filter(ctx -> ctx.getLayer().modules().stream()
                        .filter(m -> m.getName().equals(ctx.getName()))
                        .findFirst()
                        .map(Module::getDescriptor)
                        .map(desc -> desc.requires().stream()
                                .anyMatch(req -> req.name().equals(context.getName())))
                        .orElse(false))
                .collect(Collectors.toList());
    }
}
