package mg.nathafw.annotation;

import java.lang.annotation.*;
import mg.nathafw.mapping.HTTPMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface URLAnnotation {
    String value() default "";
    boolean enabled() default true;
    HTTPMethod httpMethod() default HTTPMethod.GET;
}
