package com.example.scan.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 14:57 2020/9/4
 *
 * @author zmp
 * <p>
 * des:
 */
//注解的位置  类，方法,属性，构造方法,参数等
@Target({
        ElementType.TYPE, //类接口
        ElementType.METHOD,//方法
        ElementType.FIELD,//属性
        ElementType.PARAMETER,//参数
        ElementType.CONSTRUCTOR,//构造方法
        ElementType.ANNOTATION_TYPE,//注解器
        ElementType.LOCAL_VARIABLE,//局部变量
        ElementType.PACKAGE, //包
//        ElementType.TYPE_USE //任意类型

})


//注解的有效时间
@Retention(RetentionPolicy.RUNTIME)
/*
 * RetentionPolicy.SOURCE:当前注解编译期可见，不会写入 class 文件
 * RetentionPolicy.CLASS:类加载阶段丢弃，会写入 class 文件
 * RetentionPolicy.RUNTIME:永久保存，可以反射获取
 */
/**
 * 被注解的类或方法可以继承给子类
 */
@Inherited

/**
 * 将注解信息写入被注解的类或方法doc文档中
 */
@Documented
public @interface MyAnnotation {

    String name() default "默认值";

}




