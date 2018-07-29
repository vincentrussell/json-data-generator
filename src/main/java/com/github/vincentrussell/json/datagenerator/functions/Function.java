package com.github.vincentrussell.json.datagenerator.functions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * helper annotation to register function classes that hold methods that are called
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Function {

    /**
     * Name or names for this function as it is called from the source json.
     *
     * @return the name
     */
    String[] name() default "";

    /**
     * This function cannot be overriden to a function with the same name;
     *
     * @return if overridable or not
     */
    boolean overridable() default false;

}
