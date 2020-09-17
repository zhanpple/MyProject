package com.example.annotation;

import com.example.lib.MyRouter;

import javax.lang.model.element.TypeElement;

/**
 * Created at 14:30 2020/9/17
 *
 * @author zmp
 * <p>
 * des:
 */
public class MyRouterBean {
    private String className;
    private String router;

    public MyRouterBean(String className, String router) {
        this.className = className;
        this.router = router;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    @Override
    public String toString() {
        return "MyRouterBean{" +
                "className='" + className + '\'' +
                ", router='" + router + '\'' +
                '}';
    }
}

