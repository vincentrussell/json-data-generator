package com.github.vincentrussell.json.datagenerator.functions;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Function {

    /**
     * Name of this function as it is called from the source json.
     *
     * @return the name
     */
    String name() default "";

    /**
     * This function cannot be overriden to a function with the same name;
     *
     * @return if overridable or not
     */
    boolean overridable() default false;

}
