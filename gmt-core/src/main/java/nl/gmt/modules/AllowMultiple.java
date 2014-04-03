package nl.gmt.modules;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AllowMultiple {
    boolean value() default true;
}
