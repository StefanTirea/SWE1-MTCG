package server.model.annotation;

import server.model.enums.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.METHOD })
@RequestMethod
public @interface Delete {

    String value() default "";

    HttpMethod method() default HttpMethod.DELETE;
}
