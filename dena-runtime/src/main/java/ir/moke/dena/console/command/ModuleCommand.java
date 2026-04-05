package ir.moke.dena.console.command;

import ir.moke.dena.console.TtyAsciiCodecs;
import ir.moke.dena.module.ModuleContext;
import ir.moke.dena.module.ModuleController;
import ir.moke.dena.module.ModuleRepository;
import org.jline.console.CommandInput;

import java.util.List;
import java.util.function.Consumer;

import static ir.moke.dena.console.ConsoleUtils.println;

public class ModuleCommand implements TtyAsciiCodecs {
    public static void moduleList(CommandInput input) {
        String line = "%s%s%-7s %-20s %-18s %-8s %-16s %s".formatted(GREEN, BOLD, "index", "name", "version", "running", "path", RESET);
        println(input, line);
        List<ModuleContext> modules = ModuleRepository.list();
        for (int i = 0; i < modules.size(); i++) {
            ModuleContext context = modules.get(i);
            boolean hasService = context.getIModule() != null;
            String index = String.valueOf(i + 1);
            String name = context.getName();
            String version = context.getVersion();
            boolean running = context.isRunning();
            String path = context.getPath().toString();
            line = "%-7s %-20s %-18s %s%-8s%s %-16s".formatted(index, name, version, running ? RESET : BACKGROUND_RED, hasService ? running : "", RESET, path);
            println(input, line);
        }
    }

    public static void moduleLoad(CommandInput input) {
        String moduleName = input.args()[0];
        ModuleController.load(moduleName);
//        processRequest(input, ModuleController::load);
    }

    public static void moduleStop(CommandInput input) {
        processRequest(input, ModuleController::stop);
    }

    public static void moduleStart(CommandInput input) {
        processRequest(input, ModuleController::start);
    }

    /*---- other methods ----*/
    private static void processRequest(CommandInput input, Consumer<String> controller) {
        try {
            int index = Integer.parseInt(input.args()[0]);
            if (index < 0) {
                println(input, "[WARN] invalid index");
                return;
            }
            ModuleContext context = ModuleRepository.list().get(index - 1);
            controller.accept(context.getName());
        } catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException) {
                println(input, "[ERROR] Invalid index");
            } else {
                println(input, "[ERROR] %s".formatted(e.getMessage()));
            }
        }
    }
}
