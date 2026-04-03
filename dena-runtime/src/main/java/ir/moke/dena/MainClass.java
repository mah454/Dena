package ir.moke.dena;

import ir.moke.dena.console.DenaCommandLine;
import ir.moke.dena.module.ModuleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MainClass {
    private static final String workDir = Optional.ofNullable(System.getenv("DENA.WORK-DIR")).orElse(System.getProperty("user.dir"));
    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    static {
        logger.info("Dena work directory: {}", workDir);
        System.setProperty("dena.work-dir", workDir);
    }

    static void main() {
        System.out.println("Application PID: " + ProcessHandle.current().pid());

        ModuleController.load("module.a");
        ModuleController.load("ir.sample");

        DenaCommandLine.run();
    }
}
