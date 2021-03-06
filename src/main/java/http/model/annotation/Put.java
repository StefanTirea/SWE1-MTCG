package http.model.annotation;

import http.model.enums.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.METHOD })
@RequestMethod
public @interface Put {

    String value() default "";

    HttpMethod method() default HttpMethod.PUT;
}
