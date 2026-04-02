package ir.moke.module.a;

import java.util.ArrayList;
import java.util.List;

public class Service {
    private final List<Person> list = new ArrayList<>();

    public void add(Person p) {
        list.add(p);
    }
}
