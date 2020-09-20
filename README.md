## 项目搭建
	1.需求分析
	2.功能模块划分
    3.基础工具类、框架、接口提取
    4.创建路由接口
    
## 路由Demo (Base moudle)
[Demo地址：https://gitee.com/zhanpples/java-project](https://gitee.com/zhanpples/java-project)
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
```
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
	优化后课通过注解生成对应的路由class键值对，但是初始化依然需要手动添加对应模块接口

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

## 终极优化（扩展 ）
	编译时注解虽然简化了使用流程，但是需要遍历dex下所以class利用反射机制完成初始化，影响启动性能
    
### 字节码插桩AMS（扩展 ）
```
//编译器无法访问对应的MyRouter$$APPA，但是可修改字节码
public void init() {
     new MyRouter$$APPA().addRouter(mRouterMap);
     new MyRouter$$APPB().addRouter(mRouterMap);
     new MyRouter$$APPC().addRouter(mRouterMap);
}
```
### 更多组件化功能可参考Arouter
https://github.com/alibaba/ARouter

