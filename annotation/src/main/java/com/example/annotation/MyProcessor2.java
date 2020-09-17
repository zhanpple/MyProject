package com.example.annotation;

import com.example.lib.FindViewByID;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

//import com.google.auto.service.AutoService;

/**
 * Created by zmp on 2019/5/10 16:31
 *
 * @author zmp
 */
//@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.lib.FindViewByID"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyProcessor2 extends AbstractProcessor {

        private Map<String, List<VariableInfo>> classMap = new HashMap<>();

        private Map<String, TypeElement> classTypeElement = new HashMap<>();

        private Filer filer;

        private Elements elementUtils;

        @Override
        public synchronized void init(ProcessingEnvironment processingEnvironment) {
                super.init(processingEnvironment);
                filer = processingEnv.getFiler();
                elementUtils = processingEnv.getElementUtils();
                System.out.println("-----------------------------------------------");
        }

        @Override
        public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
                collectInfo(roundEnvironment);
                writeToFile();
                return true;
        }

        /**
         * @param roundEnvironment
         */
        private void collectInfo(RoundEnvironment roundEnvironment) {
                classMap.clear();
                classTypeElement.clear();

                Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindViewByID.class);
                for (Element element : elements) {
                        int viewId = element.getAnnotation(FindViewByID.class).value();

                        VariableElement variableElement = (VariableElement) element;

                        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                        String classFullName = typeElement.getQualifiedName().toString();

                        List<VariableInfo> variableList = classMap.get(classFullName);
                        if (variableList == null) {
                                variableList = new ArrayList<>();
                                classMap.put(classFullName, variableList);

                                classTypeElement.put(classFullName, typeElement);
                        }
                        VariableInfo variableInfo = new VariableInfo();
                        variableInfo.setVariableElement(variableElement);
                        variableInfo.setViewId(viewId);
                        variableList.add(variableInfo);
                }
        }

        private void writeToFile() {
                try {
                        for (String classFullName : classMap.keySet()) {
                                TypeElement typeElement = classTypeElement.get(classFullName);

                                MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                                        .addModifiers(Modifier.PUBLIC)
                                        .addJavadoc("Init $L Views.\n@param activity $L\n", typeElement.getSimpleName().toString(), classFullName)
                                        .addParameter(ParameterSpec.builder(TypeName.get(typeElement.asType()), "activity").build());
                                List<VariableInfo> variableList = classMap.get(classFullName);
                                for (VariableInfo variableInfo : variableList) {
                                        VariableElement variableElement = variableInfo.getVariableElement();
                                        String variableName = variableElement.getSimpleName().toString();
                                        String variableFullName = variableElement.asType().toString();
                                        constructor.addStatement("activity.$L=($L)activity.findViewById($L)", variableName, variableFullName, variableInfo.getViewId());
                                }
                                TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "$$ViewInjector")
                                        .addModifiers(Modifier.PUBLIC)
                                        .addMethod(constructor.build())
                                        .build();

                                String packageFullName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                                JavaFile javaFile = JavaFile.builder(packageFullName, typeSpec)
                                        .build();
                                javaFile.writeTo(filer);
                        }
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

}
