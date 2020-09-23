## 项目搭建
	1.需求分析
	2.功能模块划分
    3.基础工具类、框架、接口提取
    4.创建路由接口
    
## 路由Demo (Base moudle)
```
//路由基础工具类
public class RouterTools {
    private static volatile RouterTools mRouterTools;

    private static HashMap<String, Class<? extends Activity>> mRouterMap;

    private RouterTools() {
        mRouterMap = new HashMap<>();
    }

    public static RouterTools getInstance() {
        if (mRouterTools == null) {
            synchronized (RouterTools.class) {
                if (mRouterTools == null) {
                    mRouterTools = new RouterTools();
                }
            }
        }
        return mRouterTools;
    }

	//添加路由与Activity对应
    public void addRouter(String router, Class<? extends Activity> clazz) {
        mRouterMap.put(router, clazz);
    }

    public void navigate(Context context, String router) {
        context.startActivity(new Intent(context, mRouterMap.get(router)));
    }
}
```
## 路由初始化(APP启动moudle)
```
RouterTools.getInstance().addRouter("A/AMainActviity",AMainActivity.class);
RouterTools.getInstance().addRouter("B/AMainActviity",BMainActivity.class);
RouterTools.getInstance().addRouter("C/AMainActviity",CMainActivity.class);
...
...
...
//使用
RouterTools.getInstance().navigate(context,"A/AMainActviity");
```
## 路由框架优化
	虽然路由功能实现类，但是初始化过程比较复杂，使用过程需要收到addRouter对应关系，对开发者不友好
### 编译时注解自动生成初始化代码
```java
    compileOnly 'com.google.auto.service:auto-service:1.0-rc7'
    kapt'com.google.auto.service:auto-service:1.0-rc7'
    implementation 'com.squareup:javapoet:1.12.1'
```
```java
//注册注解器
@AutoService(Processor.class)
//添加要监听对注解
@SupportedAnnotationTypes({"com.example.annotation.MyRouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//监听模块Options 实现模块分包
@SupportedOptions({"MODULE_NAME"})
public class MyRouterProcessor extends AbstractProcessor {
}
```
### 使用
```
defaultConfig {
       	...
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [MODULE_NAME: project.getName()]
            }
        }
    }
    
dependencies {
   kapt project(':annotation')
}

@MyRouter("appb/BMainActivity")
class BMainActivity : AppCompatActivity() {
}

```
### 自动生成带MyRouter注解对应模块接口代码
```
public class MyRouter$$APPC implements IRouter {
  /**
   * addRouter
   * @routerMap 路由map
   */
  @Override
  public void addRouter(HashMap<String, Class<? extends android.app.Activity>> routerMap) {
    routerMap.put("appc/CMainActivity",com.zhanpple.appc.CMainActivity.class);
  }
}

```
### 初始化优化 （App模块）
```
//需要将对应模块接口传递给baseMoudlue
RouterTools.getInstance().init(new MyRouter$$APPA(),new MyRouter$$APPB(),new MyRouter$$APPC()...)

//基础模块
public class RouterTools{
  public void init(IRouter... iRouters) {
        for (IRouter iRouter : iRouters) {
            iRouter.addRouter(mRouterMap);
        }
    }
 }

```
## 二次优化
	优化后可通过注解生成对应的路由class键值对，但是初始化依然需要手动添加对应模块接口

### 反射获取对应IRouter类自动完成注册
```
//获取对象apk 遍历对应dex文件下的class，反射调用addRouter
public void init(Context context) {
        String packageResourcePath = context.getApplicationContext().getPackageResourcePath();
        try {
    
            DexFile dexFile = new DexFile(packageResourcePath);
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                String element = entries.nextElement();
                Log.e("RouterTools", "init: " + element);
                if (element.contains("com.example.router")) {
                    Log.e("RouterTools", "init: " + element);
                    Class<?> aClass = Class.forName(element);
                    int modifiers = aClass.getModifiers();
                    if (IRouter.class.isAssignableFrom(aClass) && !Modifier.isInterface(modifiers)) {
                        ((IRouter) aClass.newInstance()).addRouter(mRouterMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RouterTools", "init: ", e);
        }

```
```
初始化
RouterTools.getInstance().init(context)

```

## 终极优化
```
 public void init(Context context) {
        loadRouterMap();
        if (isUseAms) {//如果调用了register方法就不需要遍历dex
            return;
        }
       	....
        ....
}

private static void loadRouterMap() {
        isUseAms = false;
        //在此处完成字节码插装
        //register("com.example.router.appc.MyRouter$$APPC")
        //register("com.example.router.appb.MyRouter$$APPB")
}
//提供给字节码插桩调用
private static void register(String className) {
        isUseAms = true;
        Log.e("RouterTools", "register: " + className);
        try {
            Class<?> aClass = Class.forName(className);
            int modifiers = aClass.getModifiers();
            if (IRouter.class.isAssignableFrom(aClass) && !Modifier.isInterface(modifiers)) {
                ((IRouter) aClass.newInstance()).addRouter(mRouterMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
### plugin插件
```
public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        Logger.make(project);
        Logger.w("apply:init-----------------");
        boolean hasAppPlugin = project.getPlugins().hasPlugin(AppPlugin.class);
        Logger.w("apply:init-----------------" + hasAppPlugin);
        if (hasAppPlugin) {

            Logger.w("apply:init-----------------" + hasAppPlugin);
            AppExtension android = project.getExtensions().getByType(AppExtension.class);

            //register this plugin
            android.registerTransform(new MyRouterTransform(project));
        }
    }
}
```
### 执行gradle插件对应publishing的publishToMavenLocal发布到mavenLocal()
```
apply plugin: 'java-library'
apply plugin: 'kotlin'

apply plugin: 'maven'
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation gradleApi()
    implementation 'com.android.tools.build:gradle:4.0.1'
}

apply plugin: 'maven-publish'

publishing {
    publications {
        mavenJava(MavenPublication) {
            groupId 'com.example.android'
            artifactId  "zhanpple"
            version 1.0
            from components.java
            // more goes in here
        }
    }

    repositories {
        mavenLocal()
    }
}



sourceCompatibility = "1.7"
targetCompatibility = "1.7"
```

### Transform+字节码插桩AMS
```java
package com.example.plugin.transform;

import com.example.plugin.tools.Logger;
import com.example.plugin.tools.ScanUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;



/**
 * Created at 9:41 2020/9/23
 *
 * @author zmp
 * <p>
 * des:定义自己的Transform
 */
public class MyRouterTransform extends MyBaseTransform {

    public MyRouterTransform(Project project) {
        super(project);
    }

	//扫描获取注解类和要插桩的工具类
    protected void transformJar(File input, File dest) {
        if (ScanUtil.shouldProcessPreDexJar(input.getAbsolutePath())) {
            ScanUtil.scanJar(input, dest);
        } else {
            super.transformJar(input, dest);
        }
    }

	//扫描获取注解类和要插桩的工具类
    protected void transformSingleFile(String parentPath, File input, File dest) {
        if (!parentPath.endsWith(File.separator)) {
            parentPath += File.separator;
        }
        String replace = input.getAbsolutePath().replace(parentPath, "");
        if (!File.separator.equals("/")) {
            replace = replace.replaceAll("\\\\", "/");
        }
        try {
            if (ScanUtil.shouldProcessClass(replace)) {
                ScanUtil.scanClass(input, dest);
            }
            FileUtils.copyFile(input, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	//扫描获取注解类和要插桩的工具类
    protected void insertInitCodeTo() {
        File containsInitClass = ScanUtil.fileContainsInitClass;
        Logger.e("containsInitClass：" + containsInitClass);
        Logger.e("containsInitClass：" + Arrays.toString(ScanUtil.CLASS_NAMES.toArray()));
        if (containsInitClass != null) {
            insertInitCodeIntoJarFile(containsInitClass);
        }
    }

    /**
     * generate code into jar file
     *
     * @param jarFile the jar file which contains LogisticsCenter.class
     * @return
     */
    private void insertInitCodeIntoJarFile(File jarFile) {
        try {
            File optJar = new File(jarFile.getParent(), jarFile.getName() + ".opt");
            if (optJar.exists()) {
                optJar.delete();
            }
            JarFile file = new JarFile(jarFile);
            Enumeration<JarEntry> enumeration = file.entries();
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = file.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(zipEntry);
                if (ScanUtil.ASM_CLASS_NAME.equals(entryName)) {
                    byte[] bytes = referHackWhenInit(inputStream);
                    jarOutputStream.write(bytes);
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                inputStream.close();
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            file.close();

            if (jarFile.exists()) {
                jarFile.delete();
            }
            optJar.renameTo(jarFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //refer hack class when object init
    private byte[] referHackWhenInit(InputStream inputStream) {
        Logger.e("referHackWhenInit");
        byte[] bytes = null;
        try {
            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            bytes = cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            //generate code into this method
            Logger.e("visitMethod:" + name);
            if (name.equals(ScanUtil.GENERATE_TO_METHOD_NAME)) {
                mv = new RouteMethodVisitor(api, mv, access, name, desc);
            }
            return mv;
        }
    }

    static class RouteMethodVisitor extends AdviceAdapter {

        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor (see  Type).
         */
        protected RouteMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            //在loadRouterMap里插入代码
            for (String className : ScanUtil.CLASS_NAMES) {
                push(className);
                invokeStatic(Type.getType(ScanUtil.ASM_CLASS_NAME_TYPE), new Method("register", "(Ljava/lang/String;)V"));
            }
        }
		
        //防止栈溢出 maxStack + Integer.MAX_VALUE
        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + Integer.MAX_VALUE, maxLocals);
        }
    }

}

```

### ScanUtil 扫描工具类
```java
package com.example.plugin.tools;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ScanUtil {

    public static final String FIND_CLASS_PACKAGE_NAME = "com/example/router";

    public static final String INTERFACE_NAME = "com/example/basemoudle/IRouter";

    public static final String ASM_CLASS_NAME = "com/example/basemoudle/RouterTools.class";

    public static final String ASM_CLASS_NAME_TYPE = "Lcom/example/basemoudle/RouterTools;";

    public static final ArrayList<String> CLASS_NAMES = new ArrayList<>();

    public static final String GENERATE_TO_METHOD_NAME = "loadRouterMap";

    public static File fileContainsInitClass;

    /**
     * scan jar file
     *
     * @param jarFile  All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    public static void scanJar(File jarFile, File destFile) {
        try {
            JarFile file = new JarFile(jarFile);
            Enumeration<JarEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(FIND_CLASS_PACKAGE_NAME)) {
                    Logger.w("scanJar-----------------" + jarEntry);
                    InputStream inputStream = file.getInputStream(jarEntry);
                    scanClass(inputStream);
                    inputStream.close();
                } else if (ASM_CLASS_NAME.equals(entryName)) {
                    //找到要插桩的类
                    fileContainsInitClass = destFile;
                } else {
                    Logger.w("scanJar:" + entryName);
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.copyFile(jarFile, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository");
    }

    public static boolean shouldProcessClass(String entryName) {
        Logger.e("entryName:" + entryName);
        return entryName != null && entryName.endsWith(".class") && entryName.startsWith(FIND_CLASS_PACKAGE_NAME);
    }

    /**
     * scan class file
     *
     * @param file class
     */
    public static void scanClass(File file, File desFile) {
        if (file.getAbsolutePath().contains("com/example/basemoudle/RouterTools")) {
            fileContainsInitClass = desFile;
            return;
        }
        try {
            scanClass(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void scanClass(InputStream inputStream) {
        try {
            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(cr, 0);
            ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM7, cw);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            if (interfaces != null) {
                for (String anInterface : interfaces) {
                    if (anInterface.equals(INTERFACE_NAME)) {
                        //fix repeated inject init code when Multi-channel packaging
                        if (!CLASS_NAMES.contains(name)) {
                            CLASS_NAMES.add(name);
                            Logger.w("add:------------------:" + name);
                        }
                    }
                }
            }
        }
    }
}
```
### [参考代码 https://github.com/alibaba/ARouter](https://github.com/alibaba/ARouter)

