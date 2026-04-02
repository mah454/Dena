package ir.moke.module.b;

import ir.moke.dena.api.IModule;

import java.util.Timer;

public class ModuleRunner implements IModule {
    private final Timer timer = new Timer("Sample Timer Module");

    @Override
    public void start() {
        timer.schedule(new TaskExecutor(), 0, 2000);
    }

    @Override
    public void stop() {
        timer.purge();
        timer.cancel();
    }
}
