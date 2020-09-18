
package com.example.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zmp on 2019/5/10 17:37
 *
 * @author zmp
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface MyRouter {

        String value();

}

