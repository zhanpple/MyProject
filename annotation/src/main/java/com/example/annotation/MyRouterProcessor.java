package com.example.annotation;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

/**
 * Created by zmp on 2019/5/10 16:31
 * 路由注解器
 *
 * @author zmp
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.annotation.MyRouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({"MODULE_NAME"})
public class MyRouterProcessor extends AbstractProcessor {

    private Filer filer;

    private Elements elementUtils;

    private Messager printMessage;

    private ArrayList<MyRouterBean> classNames = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        printMessage = processingEnv.getMessager();
        println("init-----------------------------------------------");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        classNames.clear();
        println("process-----------------------------------------------");
        String module_name = processingEnv.getOptions().get("MODULE_NAME");
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(MyRouter.class);
        for (Element element : elements) {
            if (element instanceof TypeElement) {//类注解
                TypeElement typeElement = (TypeElement) element;
                PackageElement packageOf = elementUtils.getPackageOf(typeElement);
                Name qualifiedName = typeElement.getQualifiedName();
                println(packageOf.getQualifiedName().toString() + "--" + qualifiedName);
                MyRouter annotation = typeElement.getAnnotation(MyRouter.class);
                classNames.add(new MyRouterBean(qualifiedName.toString(), annotation.value()));
            }
        }
        println("process---------------------2--------------------------");
        for (TypeElement typeElement : set) {
            PackageElement packageOf = elementUtils.getPackageOf(typeElement);
            Name qualifiedName = typeElement.getQualifiedName();
            println(packageOf.getQualifiedName().toString() + "--" + qualifiedName);
        }
        try {
            writeToFile(module_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void writeToFile(String module_name) throws IOException {
        if (classNames.isEmpty()) {
            return;
        }
        //ClassName会自动倒入包
        ClassName string = ClassName.get("java.lang",
                "String");
        ClassName hashMap = ClassName.get("java.util",
                "HashMap");
        ClassName activity = ClassName.get("",
                "? extends android.app.Activity");
        ClassName clazz = ClassName.get("java.lang",
                "Class");

        //泛形参数
        ParameterizedTypeName typeName = ParameterizedTypeName.get(clazz, activity);
        ParameterizedTypeName type = ParameterizedTypeName.get(hashMap, string, typeName);
        MethodSpec.Builder addRouter = MethodSpec.methodBuilder("addRouter")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addJavadoc("addRouter\r\n@routerMap 路由map")
                .returns(TypeName.VOID)
                .addParameter(
                        type
                        ,"routerMap");
        ClassName routerTools = ClassName.get("com.example.basemoudle",
                "RouterTools");
        for (MyRouterBean className : classNames) {
//            addRouter.addStatement("$T.getInstance().addRouter(\"$L\",$L.class)",routerTools, className.getRouter(), className.getClassName());
            addRouter.addStatement("routerMap.put(\"$L\",$L.class)", className.getRouter(), className.getClassName());
        }

        TypeSpec typeSpec = TypeSpec.classBuilder("MyRouter$$" + module_name.toUpperCase())
                .addSuperinterface( ClassName.get("com.example.basemoudle","IRouter"))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(addRouter.build())
//                .addAnnotation(AnnotationSpec
//                        .builder(MyRouter.class)
//                        .addMember("value","value=$L","2")
//                .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.router." + module_name.toLowerCase(), typeSpec)
                .build();
        javaFile.writeTo(filer);
    }


    private void println(String msg) {
        printMessage.printMessage(Diagnostic.Kind.NOTE,
                "MyProcessor:" + msg + "\r\n");
    }

}
