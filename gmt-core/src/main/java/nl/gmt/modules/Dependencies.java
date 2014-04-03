package nl.gmt.modules;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Dependencies {
    Class<?>[] value();
}
