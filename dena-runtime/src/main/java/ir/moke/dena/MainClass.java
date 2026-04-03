package ir.moke.dena;

import ir.moke.dena.console.DenaCommandRegistry;
import ir.moke.dena.module.ModuleController;
import org.jline.builtins.ConfigurationPath;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

public class MainClass {
    private static final String workDir = Optional.ofNullable(System.getenv("DENA.WORK-DIR")).orElse(System.getProperty("user.dir"));
    private static final Path workDirPath = Paths.get(workDir);
    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    static {
        logger.info("Dena work directory: {}", workDir);
        System.setProperty("dena.work-dir", workDir);
    }

    static void main() {
        System.out.println("Application PID: " + ProcessHandle.current().pid());

        ModuleController.load("module.a");
        ModuleController.load("ir.sample");

        try {
            Terminal terminal = TerminalBuilder.builder().build();
            Parser parser = new DefaultParser();
            //
            // Command registers
            //
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            ConfigurationPath configPath = new ConfigurationPath(workDirPath, workDirPath);
            DenaCommandRegistry denaCommandRegistry = new DenaCommandRegistry();
            SystemRegistryImpl system = new SystemRegistryImpl(parser, terminal, workDir, configPath);

            system.setCommandRegistries(denaCommandRegistry);
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

            denaCommandRegistry.setLineReader(reader);
            //
            // REPL-loop
            //
            while (true) {
                try {
                    system.cleanUp();
                    String line = reader.readLine("prompt> ", null, (MaskingCallback) null, null);
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
