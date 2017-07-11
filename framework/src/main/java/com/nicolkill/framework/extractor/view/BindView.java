package com.nicolkill.framework.extractor.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nicolkill on 7/6/17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BindView {

    int NO_ID = 0;

    int value() default NO_ID;

}
