package com.ctrip.car.osd.framework.aspectj.cache;

import com.ctrip.car.osd.framework.aspectj.util.ResponseVerifyUtility;
import com.ctrip.car.osd.framework.aspectj.util.SpringBeanUtility;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctriposs.baiji.rpc.common.HasResponseStatus;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * 验证空结果工具bean缓存
 */
public class VerifyUtilCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyUtilCache.class);
    //自定义验证method缓存,用于反射运行,运行时生成的bean,method里的类型不一致
    private static final Map<Class<? extends HasResponseStatus>, Method> VERIFY_METHOD_CACHE = new ConcurrentHashMap<>(32);
    //使用guava缓存该response对应的bean验证工具
    private static LoadingCache<Class<? extends HasResponseStatus>, ResponseVerifyUtility> VERIFY_UTILITY_CACHE = CacheBuilder.newBuilder().recordStats()
            .build(new CacheLoader<Class<? extends HasResponseStatus>, ResponseVerifyUtility>() {

                @Override
                public ResponseVerifyUtility load(Class<? extends HasResponseStatus> key) throws ExecutionException {
                    ResponseVerifyUtility responseVerifyUtil = null;
                    try {
                        responseVerifyUtil = getResponseVerifyUtil(key);
                        if (responseVerifyUtil != null) {
                            VERIFY_METHOD_CACHE.putIfAbsent(key, getVerifyMethod(key, responseVerifyUtil));
                        }
                    } catch (Exception e) {
                        throw new ExecutionException(e);
                    }
                    return responseVerifyUtil;
                }
            });

    /**
     * 根据class获取对应的验证工具bean
     *
     * @param key
     * @return
     * @throws ExecutionException
     */
    public static final ResponseVerifyUtility get(Class<? extends HasResponseStatus> key) throws ExecutionException {
        if (key == null) {
            return null;
        }
        return VERIFY_UTILITY_CACHE.get(key);
    }

    /**
     * 将验证bean和method放入缓存池
     *
     * @param key
     * @param value
     */
    public static void putIfAbsent(Class<? extends HasResponseStatus> key, ResponseVerifyUtility value) {
        VERIFY_UTILITY_CACHE.asMap().putIfAbsent(key, value);
        Method verify = getVerifyMethod(key, value);
        VERIFY_METHOD_CACHE.putIfAbsent(key, verify);
    }

    public static final Method getVerifyMethod(Class<? extends HasResponseStatus> key) {
        return VERIFY_METHOD_CACHE.get(key);
    }

    private static final Method getVerifyMethod(Class<? extends HasResponseStatus> key, ResponseVerifyUtility value) {
        if (key == null || value == null) {
            return null;
        }
        try {
            return value.getClass().getMethod("verify", key);
        } catch (Exception e) {
            LOGGER.warn("no verify method", e);
        }
        return null;
    }

    private static final ClassPool pool = ClassPool.getDefault();
    private static CtClass CT_CLASS_PARENT;
    private static CtMethod VERIFY_COPY_METHOD;
    static {
        try {
            Class<ResponseVerifyUtility> responseVerifyUtilityClass = ResponseVerifyUtility.class;
            ClassClassPath classPath = new ClassClassPath(responseVerifyUtilityClass);
            pool.insertClassPath(classPath);
            CT_CLASS_PARENT = pool.getCtClass(responseVerifyUtilityClass.getName());
            VERIFY_COPY_METHOD = CT_CLASS_PARENT.getDeclaredMethod("verifyCopy");
           /* CtMethod[] declaredMethods = CT_CLASS_PARENT.getDeclaredMethods();
            Optional<CtMethod> verifyCopy = Stream.of(declaredMethods).filter(method -> StringUtils.equalsIgnoreCase(method.getName(), "verifyCopy") && method.getModifiers() == Modifier.PRIVATE).findAny();
            VERIFY_COPY_METHOD = verifyCopy.get();
            System.out.println("VERIFY_COPY_METHOD->"+VERIFY_COPY_METHOD);*/
        } catch (NotFoundException e) {
            LOGGER.warn("init ResponseVerifyUtility ClassClassPath error!", e);
        }
    }

    private static final ResponseVerifyUtility getResponseVerifyUtil(Class<?> aClass) throws Exception {
        Object verifyBean;//ClassPool：CtClass对象的容器
        Class<ResponseVerifyUtility> responseVerifyUtilityClass = ResponseVerifyUtility.class;
        String aClassName = aClass.getName();
        //通过ClassPool生成一个public新类,并制定父类
        CtClass ctClass = pool.makeClass(aClassName + "EmptyResponseVerifyUtil", CT_CLASS_PARENT);
        //设置父类的泛型类型
        SignatureAttribute.TypeArgument typeArgument = new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(aClassName, null));
        SignatureAttribute.ClassType classType = new SignatureAttribute.ClassType(responseVerifyUtilityClass.getName(), ArrayUtils.toArray(typeArgument));
        ctClass.setGenericSignature(classType.encode());

        //添加自定义方法,赋值方法到新方法中
        CtMethod ctMethod = CtNewMethod.copy(VERIFY_COPY_METHOD, ctClass, null);
        ctMethod.addParameter(pool.getCtClass(aClassName));
        // 修改方法名称
        ctMethod.setName("verify");
        ctMethod.setModifiers(Modifier.PUBLIC);
        ctMethod.setBody("return super.commonVerify($$);");
        /*
        StringBuilder methodBody = new StringBuilder("public boolean verify(");
        methodBody.append(aClassName).append(" response)").append("{");
        methodBody.append("return super.commonVerify(response);").append("}");
        CtMethod ctMethod = CtMethod.make(methodBody.toString(), ctClass);*/
        ctClass.addMethod(ctMethod);
        Class<?> clazz = ctClass.toClass();
       // ctClass.writeFile("/javassist");
        // 获取bean工厂并转换为DefaultListableBeanFactory
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) SpringBeanUtility.getApplicationContext();
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        // 注册bean
        defaultListableBeanFactory.registerBeanDefinition(clazz.getSimpleName(), beanDefinitionBuilder.getRawBeanDefinition());
        verifyBean = SpringBeanUtility.getBean(clazz);
        return (ResponseVerifyUtility) verifyBean;
    }
}
