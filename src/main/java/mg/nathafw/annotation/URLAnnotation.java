package mg.nathafw.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface URLAnnotation {
    String value() default "";
    boolean enabled() default true;
}
