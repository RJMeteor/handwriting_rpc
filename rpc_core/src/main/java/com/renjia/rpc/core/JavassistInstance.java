package com.renjia.rpc.core;

import cn.hutool.core.util.IdUtil;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.anno.RpcFetch;
import com.renjia.rpc.loadBalancer.LoadBalancer;
import com.renjia.rpc.retry.Retry;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.tolerant.FaultTolerant;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class JavassistInstance {
    private final ClassPool pool;
    private final CtClass ctClass;
    private final Class<?> interfaceClass;

    public JavassistInstance(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        pool = ClassPool.getDefault();
        ctClass = pool.makeClass(interfaceClass.getName() + IdUtil.getSnowflakeNextIdStr() + "Instance");
    }

    public Object dynamicGenerateClass() {
        if (!interfaceClass.isInterface() && interfaceClass.getAnnotation(RpcFetch.class) == null) return null;
        ctClass.addInterface(pool.makeInterface(interfaceClass.getName()));
        addMethod();
        return addClassAnnotaion();
    }

    @SneakyThrows
    private Object addClassAnnotaion() {
        RpcFetch annotation = interfaceClass.getAnnotation(RpcFetch.class);
        ConstPool constPool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation scopeAnnotation = new Annotation(RpcFetch.class.getName(), constPool);
        scopeAnnotation.addMemberValue("serverName", new StringMemberValue(annotation.serverName(), constPool));
        scopeAnnotation.addMemberValue("loadBalancer",
                resultEnumMember(
                        constPool, new String[]{LoadBalancer.Type.class.getName(),
                                annotation.loadBalancer().name()})
        );
        scopeAnnotation.addMemberValue("retry",
                resultEnumMember(
                        constPool, new String[]{Retry.Type.class.getName(),
                                annotation.retry().name()})
        );
        scopeAnnotation.addMemberValue("tolerant", new ClassMemberValue(annotation.tolerant().getName(), constPool));
        annotationsAttribute.addAnnotation(scopeAnnotation);
        ctClass.getClassFile().addAttribute(annotationsAttribute);
//        ctClass.writeFile("./");
        Class<?> aClass = ctClass.toClass();
        Object o = aClass.newInstance();
        return o;
    }

    @SneakyThrows
    private void addMethod() {
        Method[] declaredMethods = interfaceClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getAnnotation(RequestInfo.class) == null) continue;
            StringBuilder stringBuilder = new StringBuilder();
            String returnType = method.getReturnType().getName();
            stringBuilder.append("public ")
                    .append(returnType)
                    .append(" ")
                    .append(method.getName())
                    .append("(");
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                stringBuilder.append(parameter.getType().getName())
                        .append(" ")
                        .append(parameter.getName());
                if (i != parameters.length - 1) {
                    stringBuilder.append(",");
                }
            }
            if (returnType != "void") stringBuilder.append("){return null;}");
            else stringBuilder.append("){}");
            CtMethod make = CtMethod.make(stringBuilder.toString(), ctClass);
            addAnnotation(make, method);
            ctClass.addMethod(make);
        }
    }

    private void addAnnotation(CtMethod make, Method method) {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        RequestInfo info = method.getAnnotation(RequestInfo.class);
        StringMemberValue stringValue = new StringMemberValue(
                info.requesetUrl(), constPool);
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(RequestInfo.class.getName(), constPool);

        annotation.addMemberValue("requestType",
                resultEnumMember(constPool, new String[]{RequestInfo.RequestType.class.getName(),
                        info.requestType().name()}));


        annotation.addMemberValue("requesetUrl", stringValue);
        attribute.addAnnotation(annotation);
        make.getMethodInfo().addAttribute(attribute);
    }

    private EnumMemberValue resultEnumMember(ConstPool constPool, String[] strings) {
        EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
        enumMemberValue.setType(strings[0]);
        enumMemberValue.setValue(strings[1]);
        return enumMemberValue;
    }
}
