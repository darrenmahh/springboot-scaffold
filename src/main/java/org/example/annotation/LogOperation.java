package org.example.annotation;

import java.lang.annotation.*;

// 指定当前注解只能添加在方法上   不能添加在类 构造器等上
@Target(ElementType.METHOD)
// 给当前代码加上标签 使得程序在运行的时候可以看见这个注解
@Retention(RetentionPolicy.RUNTIME)
// 在生成文档的时候将标签也加上
@Documented
public @interface LogOperation {
    String value() default "";

    String module() default "";

    boolean logParams() default true;

    boolean logResult() default true;
}
