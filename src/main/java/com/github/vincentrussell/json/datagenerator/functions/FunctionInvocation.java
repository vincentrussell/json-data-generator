package com.github.vincentrussell.json.datagenerator.functions;

import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@interface FunctionInvocation {}
