package ir.moke.dena.console.command;

import org.jline.console.CommandInput;

import static ir.moke.dena.console.ConsoleUtils.println;

public class SystemCommand {
    public static void exit(CommandInput input) {
        System.exit(0);
    }

    public static void gc(CommandInput input) {
        println(input, "[*] Call GC");
        System.gc();
    }

    public static void pid(CommandInput input) {
        long pid = ProcessHandle.current().pid();
        println(input, "[*] PID: %s".formatted(pid));
    }
}
