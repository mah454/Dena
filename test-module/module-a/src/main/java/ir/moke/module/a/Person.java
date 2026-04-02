package ir.moke.module.a;

import ir.moke.utils.json.JsonUtils;

import java.time.LocalDateTime;

public record Person(String name, LocalDateTime dateTime) {
    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
