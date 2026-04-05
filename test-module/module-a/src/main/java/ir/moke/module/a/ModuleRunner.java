package ir.moke.module.a;

import ir.moke.dena.api.IModule;
import ir.moke.utils.json.JsonUtils;

import java.time.LocalDateTime;

public class ModuleRunner implements IModule {
    @Override
    public void start() {
        Person p = new Person("test", LocalDateTime.now());
        System.out.println(JsonUtils.toJson(p));
        System.out.println("Module Started");
    }

    @Override
    public void stop() {
        System.out.println("Module Stopped");
    }
}
