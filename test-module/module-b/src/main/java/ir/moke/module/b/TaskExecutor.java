package ir.moke.module.b;

import ir.moke.module.a.Person;
import ir.moke.module.a.Service;

import java.time.LocalDateTime;
import java.util.TimerTask;

public class TaskExecutor extends TimerTask {
    private final Service service = new Service();

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        Person p = new Person(now.toString(), now);
        service.add(p);
        System.out.println(p.toJson());
    }
}
