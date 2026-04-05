package ir.moke.module.b;

import ir.moke.module.a.Person;
import ir.moke.module.a.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.TimerTask;

public class TaskExecutor extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
    private final Service service = new Service();

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        Person p = new Person(now.toString(), now);
        service.add(p);
        logger.info(p.toJson());
    }
}
