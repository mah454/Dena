package ir.moke.module.a;

import ir.moke.dena.api.IModule;

public class ModuleRunner implements IModule {
    @Override
    public void start() {
        System.out.println("Module Started");
    }

    @Override
    public void stop() {
        System.out.println("Module Stopped");
    }
}
