package com.alan.clients.module.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String[] aliases();

    String description();

    Category category();

    int keyBind() default 0;

    boolean autoEnabled() default false;

    boolean allowDisable() default true;
}
