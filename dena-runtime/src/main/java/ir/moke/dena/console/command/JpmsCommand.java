package ir.moke.dena.console.command;

import ir.moke.dena.console.TtyAsciiCodecs;
import ir.moke.dena.jpms.ModuleContext;
import ir.moke.dena.jpms.ModuleController;
import ir.moke.dena.jpms.ModuleRepository;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.function.Consumer;


@CommandLine.Command(
        name = "jpms",
        description = "Dena JPMS Commands",
        mixinStandardHelpOptions = true,
        subcommands = {
                JpmsCommand.List.class,
                JpmsCommand.Start.class,
                JpmsCommand.Stop.class,
                JpmsCommand.Load.class,
                JpmsCommand.UnLoad.class,
        }
)
public class JpmsCommand implements Runnable, TtyAsciiCodecs {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    @CommandLine.Command(name = "list",
            mixinStandardHelpOptions = true,
            description = "List all available modules")
    static class List implements Runnable {
        @Override
        public void run() {
            String line = "%s%s%-7s %-20s %-18s %-8s %-8s %s".formatted(GREEN, BOLD, "index", "name", "version", "loaded", "running", RESET);
            System.out.println(line);
            java.util.List<ModuleContext> modules = new ArrayList<>(ModuleRepository.list());
            for (int i = 0; i < modules.size(); i++) {
                ModuleContext context = modules.get(i);
                boolean hasService = context.getIModule() != null;
                String index = String.valueOf(i + 1);
                String name = context.getName();
                String version = context.getVersion();
                boolean running = context.isRunning();
                boolean loaded = context.isLoaded();
                line = "%-7s %-20s %-18s %s%-8s%s %s%-8s%s".formatted(index, name, version, loaded ? RESET : BACKGROUND_RED, loaded, RESET, running ? RESET : hasService ? BACKGROUND_RED : RESET, hasService ? running : "", RESET);
                System.out.println(line);
            }
        }
    }

    @CommandLine.Command(name = "start",
            mixinStandardHelpOptions = true,
            description = {"Start a loaded module", "Usage: start <index>"})
    static class Start implements Runnable {
        @CommandLine.Parameters
        private Integer index;

        @Override
        public void run() {
            processRequest(index, ModuleController::start);
        }
    }

    @CommandLine.Command(name = "stop",
            mixinStandardHelpOptions = true,
            description = {"Stop a running module", "Usage: stop <index>"})
    static class Stop implements Runnable {
        @CommandLine.Parameters
        private Integer index;

        @Override
        public void run() {
            processRequest(index, ModuleController::stop);
        }
    }

    @CommandLine.Command(name = "load",
            mixinStandardHelpOptions = true,
            description = {"Load module", "Usage: load <index>"})
    static class Load implements Runnable {
        @CommandLine.Parameters
        private Integer index;

        @Override
        public void run() {
            processRequest(index, ModuleController::load);
        }
    }

    @CommandLine.Command(name = "unload",
            mixinStandardHelpOptions = true,
            description = {"Unload module", "Usage: unload <index>"})
    static class UnLoad implements Runnable {
        @CommandLine.Parameters
        private Integer index;

        @Override
        public void run() {
            processRequest(index, ModuleController::unload);
        }
    }

    /*---- other methods ----*/
    private static void processRequest(Integer index, Consumer<String> controller) {
        try {
            if (index < 0) {
                System.out.println("[WARN] invalid index");
                return;
            }
            ModuleContext context = new ArrayList<>(ModuleRepository.list()).get(index - 1);
            controller.accept(context.getName());
        } catch (Exception e) {
            if (e instanceof IndexOutOfBoundsException) {
                System.out.printf("%s[ERROR] Invalid index%s%n", BACKGROUND_RED, RESET);
            } else {
                System.out.printf("%s[ERROR] %s%s%n", BACKGROUND_RED, e.getMessage(), RESET);
            }
        }
    }
}
