package ir.moke.dena.console;

import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class CommandLine implements TtyAsciiCodecs {
    private static final String PS1 = "%s DENA-JPMS-Prompt %s%s\uE0B0 %s".formatted(BACKGROUND_BLUE, RESET, BLUE, RESET);
    public static void run() {
        try {
            Terminal terminal = TerminalBuilder.builder().build();
            Parser parser = new DefaultParser();
            //
            // Command registers
            //
            CommandRegistry commandRegistry = new CommandRegistry();
            SystemRegistryImpl system = new SystemRegistryImpl(parser, terminal, null, null);

            system.setCommandRegistries(commandRegistry);
            //
            // Terminal & LineReader
            //
            System.out.println(terminal.getName() + ": " + terminal.getType());
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(system.completer())
                    .parser(parser)
                    .variable(LineReader.INDENTATION, 2)
                    .option(LineReader.Option.INSERT_BRACKET, true)
                    .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
                    .build();

            commandRegistry.setLineReader(reader);
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
}
