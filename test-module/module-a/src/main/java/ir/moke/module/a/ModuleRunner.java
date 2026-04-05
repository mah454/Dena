package ir.moke.module.a;

import ir.moke.dena.api.IModule;
import ir.moke.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ModuleRunner implements IModule {
    private static final Logger logger = LoggerFactory.getLogger(ModuleRunner.class);

    @Override
    public void start() {
        Person p = new Person("test", LocalDateTime.now());
        logger.info(JsonUtils.toJson(p));
        logger.info("Module Started");
    }

    @Override
    public void stop() {
        logger.info("Module Stopped");
    }
}
