package ir.moke.dena.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.MODULE)
public @interface ModuleMetadata {
    String maintainer();

    String url() default "";

    String description() default "";
}
