package org.bsdevelopment.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ICommand {
    String name() default "";

    String usage() default "";

    String description() default "";

    String style() default "/{name} {usage}";

    String consoleStyle() default "/{name} {usage} - {description}";

    String[] alias() default {""};
}