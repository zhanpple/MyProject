package com.example.scan;

import com.example.scan.annotation.MyAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created at 14:57 2020/9/4
 *
 * @author zmp
 * <p>
 * des:
 */
@MyAnnotation(name = "类注解")
public class AnnotationTest {

    @MyAnnotation(name = "构造方法注解")
    public AnnotationTest() {
        System.out.println("调用了构造方法");
        //局部变量注解不会加载大class中,无法通过反射获取
        @MyAnnotation(name = "局部变量注解")
        String myLoc = "局部变量注解";
    }

    @MyAnnotation(name = "静态方法注解")
    private static void staticMethod(@MyAnnotation(name = "参数注解") String name) {
        System.out.println("调用了静态方法:"+name);
    }

    @MyAnnotation(name = "成员方法注解")
    public void method() {
        System.out.println("调用了成员方法");
    }

    @MyAnnotation(name = "私有成员方法注解")
    protected void privateMethod() {
        System.out.println("私有成员方法注解");
    }

    @MyAnnotation(name = "属性注解")
    public String name = "我是成员变量";

    @MyAnnotation(name = "静态属性注解")
    public static String staticName = "我是静态变量";

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {


        //class 获取类注解
        Class<AnnotationTest> annotationTestClass = AnnotationTest.class;
        //getAnnotations 获取所以注解
        Annotation[] annotations = annotationTestClass.getAnnotations();
        for (Annotation annotation : annotations) {
            //instanceof 判断是否是MyAnnotation
            if (annotation instanceof MyAnnotation) {
                String className = ((MyAnnotation) annotation).name();
                System.out.println("Class:" + className);
            }
        }

//        //所有public公用方法 包括父类、接口继承的方法
//        Method[] methods = annotationTestClass.getMethods();
//        for (Method method : methods) {
//            MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
//            if (annotation != null) {
//                String className = ((MyAnnotation) annotation).name();
//                System.out.println("staticMethod:" + className);
//            }
//        }

        //获取方法注解
        //getDeclaredMethods 获取本类的所以方法 ,包括私有方法
        Method[] declaredMethods = annotationTestClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //获取指定的MyAnnotation注解
            MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
            if (annotation != null) {
                String className = ((MyAnnotation) annotation).name();
                System.out.println("Method:" + className);
            }
            int modifiers = method.getModifiers();
            if ((modifiers & 8) != 0) {//静态方法
                if (method.getName().equals("staticMethod")) {
                    Parameter[] parameters = method.getParameters();
                    for (Parameter parameter : parameters) {
                        MyAnnotation parameterAnnotation = parameter.getAnnotation(MyAnnotation.class);
                        if (parameterAnnotation != null) {
                            String className = ((MyAnnotation) parameterAnnotation).name();
                            System.out.println("parameterAnnotation:" + className);
                        }
                    }
                    method.invoke(annotationTestClass,"我是参数");
                }
            }else {
                //成员方法
                method.invoke(annotationTestClass.newInstance());
            }
        }
//
//        //class 获取构造方法
//        Constructor<?>[] constructors = annotationTestClass.getConstructors();
//        for (Constructor<?> constructor : constructors) {
//            MyAnnotation annotation = constructor.getAnnotation(MyAnnotation.class);
//            String className = ((MyAnnotation) annotation).name();
//            System.out.println("Constructor:" + className);
//            constructor.newInstance();
//        }
//
//        //获取成员变量注解
//        Field[] fields = annotationTestClass.getFields();
//        for (Field field : fields) {
//            MyAnnotation annotation = field.getAnnotation(MyAnnotation.class);
//            String className = ((MyAnnotation) annotation).name();
//            System.out.println("field:" + className);
//            int modifiers = field.getModifiers();
//            if ((modifiers & 8) != 0) {//静态变量
//                Object o = field.get(annotationTestClass);
//                System.out.println(o);
//            }else {
//                //成员变量
//                Object o = field.get(annotationTestClass.newInstance());
//                System.out.println(o);
//            }
//        }

    }

}
