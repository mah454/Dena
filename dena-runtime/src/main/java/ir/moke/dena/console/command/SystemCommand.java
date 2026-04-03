package ir.moke.dena.console.command;

import ir.moke.dena.module.ModuleContext;
import ir.moke.dena.module.ModuleController;
import ir.moke.dena.module.ModuleRepository;
import org.jline.console.CommandInput;

import static ir.moke.dena.console.ConsoleUtils.println;

public class SystemCommand {
    public static void exit(CommandInput input) {
        ModuleRepository.list().stream().map(ModuleContext::getName).forEach(ModuleController::stop);
        System.exit(0);
    }

    public static void gc(CommandInput input) {
        println(input, "[*] Call GC");
        System.gc();
    }
}
