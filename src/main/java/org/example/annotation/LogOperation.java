package org.example.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    String value() default "";

    String module() default "";

    boolean logParams() default true;

    boolean logResult() default true;
}
