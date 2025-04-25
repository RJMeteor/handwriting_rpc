package com.renjia.rpc.apply.config;

import com.renjia.rpc.apply.EnableRPCAutoServer;
import com.renjia.rpc.apply.ProxyRpcFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Set;

@ConditionalOnClass(name = {"org.springframework.web.servlet.DispatcherServlet"})
public class SpringStarterBeanRpcServer implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 创建一个 ClassPathScanningCandidateComponentProvider 实例
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        MergedAnnotations annotations = importingClassMetadata.getAnnotations();
        MergedAnnotation<EnableRPCAutoServer> enableRPCAutoServerMergedAnnotation = annotations.get(EnableRPCAutoServer.class);
        String scanPackage = enableRPCAutoServerMergedAnnotation.getString("scanPackage");
        // 添加过滤器，只扫描接口
        scanner.addIncludeFilter(new AssignableTypeFilter(Class.class) {
            @Override
            protected boolean matchClassName(String className) {
                try {
                    // 使用 Spring 的 ClassUtils 获取类加载器来加载类
                    Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
                    return clazz.isInterface();
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        });
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(scanPackage);
        for (BeanDefinition candidateComponent : candidateComponents) {
            // 获取接口的全限定名
            String beanClassName = candidateComponent.getBeanClassName();
            if (beanClassName != null) {
                try {
                    // 加载接口类
                    Class<?> mapperInterface = Class.forName(beanClassName);
                    if (mapperInterface.isInterface()) {
                        // 创建 MapperFactoryBean 的 BeanDefinition
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyRpcFactoryBean.class);
                        // 设置 MapperFactoryBean 的构造函数参数为接口类
                        builder.addConstructorArgValue(mapperInterface);
                        // 获取 BeanDefinition
                        BeanDefinition beanDefinition = builder.getBeanDefinition();
                        // 注册 BeanDefinition
                        registry.registerBeanDefinition(mapperInterface.getSimpleName(), beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
