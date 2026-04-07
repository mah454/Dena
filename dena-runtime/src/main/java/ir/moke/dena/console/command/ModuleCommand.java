package ir.moke.dena.console.command;

import ir.moke.dena.console.TtyAsciiCodecs;
import ir.moke.dena.jpms.ModuleContext;
import ir.moke.dena.jpms.ModuleController;
import ir.moke.dena.jpms.ModuleRepository;
import org.jline.console.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ir.moke.dena.console.ConsoleUtils.println;

public class ModuleCommand implements TtyAsciiCodecs {
    public static void moduleList(CommandInput input) {
        String line = "%s%s%-7s %-20s %-18s %-8s %-8s %s".formatted(GREEN, BOLD, "index", "name", "version", "loaded", "running", RESET);
        println(input, line);
        List<ModuleContext> modules = new ArrayList<>(ModuleRepository.list());
        for (int i = 0; i < modules.size(); i++) {
            ModuleContext context = modules.get(i);
            boolean hasService = context.getIModule() != null;
            String index = String.valueOf(i + 1);
            String name = context.getName();
            String version = context.getVersion();
            boolean running = context.isRunning();
            boolean loaded = context.isLoaded();
            line = "%-7s %-20s %-18s %s%-8s%s %s%-8s%s".formatted(index, name, version, loaded ? RESET : BACKGROUND_RED, loaded, RESET, running ? RESET : hasService ? BACKGROUND_RED : RESET, hasService ? running : "", RESET);
            println(input, line);
        }
    }

    public static void moduleLoad(CommandInput input) {
        String moduleName = input.args()[0];
        ModuleController.load(moduleName);
    }

    public static void moduleUnload(CommandInput input) {
        processRequest(input, ModuleController::unload);
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
            ModuleContext context = new ArrayList<>(ModuleRepository.list()).get(index - 1);
            controller.accept(context.getName());
        } catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException) {
                println(input, "%s[ERROR] Invalid index%s".formatted(BACKGROUND_RED, RESET));
            } else {
                println(input, "%s[ERROR] %s%s".formatted(BACKGROUND_RED, e.getMessage(), RESET));
            }
        }
    }
}
