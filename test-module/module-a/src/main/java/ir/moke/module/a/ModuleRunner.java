package ir.moke.module.a;

import ir.moke.dena.api.IModule;
import ir.moke.utils.json.JsonUtils;

import java.time.LocalDateTime;

public class ModuleRunner implements IModule {
    @Override
    public void start() {
        try {
            System.out.println("Before");
            Person p = new Person("test", LocalDateTime.now());
            System.out.println(JsonUtils.toJson(p));
            System.out.println("Module Started");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("Module Stopped");
    }
}
