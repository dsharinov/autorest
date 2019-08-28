package com.intendia.gwt.autorest.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface AutoRestGwt {
    /**
     * Generate either reactive service model (<b>true</b>), or callback-based (<b>false</b>)
     */
    boolean rx() default true;

    /**
     * If a factory class is supplied generated service class will register itself with it.
     * Factory can be any class that implements <i>register</i> method with two parameters:
     * <ol>
     *     <li>Class of the service model;</li>
     *     <li>Instance supplier that is either {@link RxServiceSupplier} or {@link CallbackServiceSupplier}
     *     depending on <b>rx</b>.</li>
     * </ol>
     */
    Class<?> factory() default void.class;

    Class<?> factoryInterface() default void.class;
}
