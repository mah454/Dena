package ir.moke.dena.console;

import ir.moke.dena.console.command.JpmsCommand;
import ir.moke.dena.console.command.RootCommand;
import ir.moke.dena.console.command.SystemCommand;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DenaCLI implements TtyAsciiCodecs {
    private static final String PS1 = "%s DENA %s%s\uE0B0 %s".formatted(BACKGROUND_BLUE, RESET, BLUE, RESET);
    private static final Parser parser = new DefaultParser();
    private static final Terminal terminal;
    private static final SystemRegistryImpl system;
    private static final Set<CommandRegistry> registryList = new HashSet<>();
    private static final SystemCommand systemCommand = new SystemCommand();
    private static final JpmsCommand jpmsCommand = new JpmsCommand();
    public static boolean inLoop = true;

    static {
        try {
            terminal = TerminalBuilder.builder().build();
            system = new SystemRegistryImpl(parser, terminal, null, null);
            removeExitCommand();
            addCommandRegistry("Dena Available Commands", systemCommand, jpmsCommand);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void run() {
        while (true) {
            try {
                LineReader reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(system.completer())
                        .parser(parser)
                        .variable(LineReader.INDENTATION, 2)
                        .option(LineReader.Option.INSERT_BRACKET, true)
                        .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
                        .build();

                //
                // REPL-loop
                //
                while (inLoop) {
                    try {
                        system.cleanUp();
                        String line = reader.readLine(PS1, null, (MaskingCallback) null, null);
                        system.execute(line);
                    } catch (UserInterruptException e) {
                        // Ignore
                    } catch (EndOfFileException e) {
                        break;
                    } catch (Exception | Error e) {
                        system.trace(true, e);
                    }
                }
                system.close();
                inLoop = !inLoop;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addCommandRegistry(String description, Object... runnableCommands) {
        CommandLine rootCli = new CommandLine(new RootCommand());
        PicocliCommands commands = new PicocliCommands(rootCli);
        commands.name(description);
        for (Object cmd : runnableCommands) {
            String name = cmd.getClass().getDeclaredAnnotation(CommandLine.Command.class).name();
            CommandLine commandLine = new CommandLine(cmd);
            rootCli.addSubcommand(name, commandLine);
        }
        registryList.add(commands);
        system.setCommandRegistries(registryList.toArray(CommandRegistry[]::new));
        inLoop = !inLoop;
    }

    public static void removeCommandRegistry(Object runnableCommand) {
        String name = runnableCommand.getClass().getDeclaredAnnotation(CommandLine.Command.class).name();
        CommandRegistry commandRegistry = registryList.stream().filter(item -> item.commandNames().contains(name)).findFirst().orElse(null);
        if (commandRegistry != null) {
            registryList.remove(commandRegistry);
            system.setCommandRegistries(registryList.toArray(CommandRegistry[]::new));
        }
        inLoop = !inLoop;
    }

    @SuppressWarnings("unchecked")
    private static void removeExitCommand() {
        try {
            Field[] declaredFields = system.getClass().getDeclaredFields();
            Field commandExecuteField = Arrays.stream(declaredFields).filter(item -> item.getName().equalsIgnoreCase("commandExecute")).findFirst().orElse(null);
            if (commandExecuteField != null) {
                commandExecuteField.setAccessible(true);
                Map<String, CommandMethods> commandExecute = (Map<String, CommandMethods>) commandExecuteField.get(system);
                commandExecute.remove("exit");
                commandExecuteField.set(system, commandExecute);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
