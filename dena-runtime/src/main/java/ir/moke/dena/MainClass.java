package ir.moke.dena;

import ir.moke.dena.console.CommandLine;
import ir.moke.dena.console.TtyAsciiCodecs;
import ir.moke.dena.module.ModuleController;
import ir.moke.dena.utils.Logo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MainClass implements TtyAsciiCodecs {
    private static final String workDir = Optional.ofNullable(System.getenv("DENA.WORK-DIR")).orElse(System.getProperty("user.dir"));
    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    static {
        Logo.print();
        System.setProperty("dena.work-dir", workDir);
        logger.info("Dena work directory: {}", workDir);
    }

    static void main() {
        ModuleController.initStartUp();
        CommandLine.run();
    }
}
