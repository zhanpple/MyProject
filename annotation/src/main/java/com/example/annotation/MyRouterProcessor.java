package com.example.annotation;

import com.example.lib.FindViewByID;
import com.example.lib.MyRouter;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
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

//import com.google.auto.service.AutoService;

/**
 * Created by zmp on 2019/5/10 16:31
 * 路由注解器
 *
 * @author zmp
 */
//@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.lib.MyRouter"})
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
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Init module_name Router\n");


        for (MyRouterBean className : classNames) {
            constructor.addStatement("com.example.lib.RouterTools.getInstance().addRouter(\"$L\",$L.class)", className.getRouter(), className.getClassName());
        }

        TypeSpec typeSpec = TypeSpec.classBuilder("MyRouter$$" + module_name)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor.build())
                .addAnnotation(AnnotationSpec
                        .builder(FindViewByID.class)
                        .addMember("value","value=$L","2")
                .build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.lib." + module_name.toLowerCase(), typeSpec)
                .build();
        javaFile.writeTo(filer);
    }


    private void println(String msg) {
        printMessage.printMessage(Diagnostic.Kind.NOTE,
                "MyProcessor:" + msg + "\r\n");
    }

}
