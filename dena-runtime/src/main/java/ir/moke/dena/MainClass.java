package ir.moke.dena;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ir.moke.dena.console.CommandLine;
import ir.moke.dena.console.TtyAsciiCodecs;
import ir.moke.dena.jpms.ModuleController;
import ir.moke.dena.utils.Logo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MainClass implements TtyAsciiCodecs {
    private static final String workDir = Optional.ofNullable(System.getenv("DENA_WORK_DIR")).orElse(System.getProperty("user.dir"));
    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    static {
        try {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure("conf/logback.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Logo.print();
        System.setProperty("dena.work-dir", workDir);
        logger.info("Dena work directory: {}", workDir);
    }

    static void main() {
        ModuleController.initStartUp();
        CommandLine.run();
    }
}
