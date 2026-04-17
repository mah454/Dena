package ir.moke.dena.console.command;

import picocli.CommandLine;

@CommandLine.Command(
        name = "system",
        description = "System Commands",
        mixinStandardHelpOptions = true,
        subcommands = {
                SystemCommand.Shutdown.class,
                SystemCommand.ProcessId.class,
                SystemCommand.GarbageCollector.class
        }
)
public class SystemCommand implements Runnable {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    @CommandLine.Command(
            name = "shutdown",
            mixinStandardHelpOptions = true,
            description = "Shutdown system completely"
    )
    static class Shutdown implements Runnable {
        @Override
        public void run() {
            System.exit(0);
        }
    }

    @CommandLine.Command(
            name = "pid",
            mixinStandardHelpOptions = true,
            description = "Process ID"
    )
    static class ProcessId implements Runnable {
        @Override
        public void run() {
            long pid = ProcessHandle.current().pid();
            System.out.printf("[*] PID: %s%n", pid);
        }
    }

    @CommandLine.Command(
            name = "gc",
            mixinStandardHelpOptions = true,
            description = "Call jvm gc"
    )
    static class GarbageCollector implements Runnable {
        @Override
        public void run() {
            System.out.println("[*] Call GC");
            System.gc();
        }
    }
}
