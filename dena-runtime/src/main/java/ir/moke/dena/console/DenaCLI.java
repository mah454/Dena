package ir.moke.dena.console;

import org.jline.console.CommandRegistry;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DenaCLI implements TtyAsciiCodecs {
    private static final String PS1 = "%s DENA %s%s\uE0B0 %s".formatted(BACKGROUND_BLUE, RESET, BLUE, RESET);
    private static final Parser parser = new DefaultParser();
    private static final CommandRegistry denaCommandRegistry = new DenaCommandRegistry();
    private static final SystemRegistry system;
    private static final Terminal terminal;
    private static final Set<CommandRegistry> registryList = new HashSet<>();

    static {
        try {
            terminal = TerminalBuilder.builder().build();
            system = new SystemRegistryImpl(parser, terminal, null, null);
            system.setCommandRegistries(denaCommandRegistry);
            registryList.add(denaCommandRegistry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void run() {
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
            while (true) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addSystemRegistry(CommandRegistry registry) {
        registryList.add(registry);
        system.setCommandRegistries(registryList.toArray(CommandRegistry[]::new));
    }

    public static void removeCommandRegistry(CommandRegistry registry) {
        registryList.remove(registry);
        system.setCommandRegistries(registryList.toArray(CommandRegistry[]::new));
    }
}
