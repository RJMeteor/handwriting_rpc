## 🍉 手写RPC

技术栈：`etcd`(注册中心)、`vertx`（web服务器）、`guava-retrying`（重试）、`javassist`（字节码）、`hutool`、常见各种序列化技术、JDK动态代理。

设计模式：策略模式、工厂模式、单例模式、责任链模式、修饰器模式、模块方法模式、代理模式、享元模式等。



## 🍓 涉及内容

- 服务发现与注册

- 心跳检测

- 重试机制

- 负载均衡

- 容错、降级

- 消息序列化

- javassist动态生成字节码

- JDK动态代理

- 自定义Spring Starter

  

## 🌽 下载Etcd

[etcd-io/etcd: Distributed reliable key-value store for the most critical data of a distributed system (github.com)](https://github.com/etcd-io/etcd)



## 🌰 说明

该项目本是进行`TCP服务器`进行数据传输进行`远程调用`，但是会用一个问题，还要进行TCP端口连接会新开一个端口，远程和本地要有完全限定类名才能进行调用，要规定数据传输协议传递目标调用Class类型，只要本地有该Class就可以进行反射调用，这就对后面自定义`Spring Stater`就比较麻烦，因为要集成`Spring MVC`，**最后决定实现HTTP请求的方式进行远程调用（请求）`。**

基于TCP的RPC安全性高，用户可以自定义请求协议和编码解码，对细节的把控度比较高，底层可以用Netty高性能框架进行搭建。对基于TCP和HTTP的RPC的实现细节都差不多。

### Vertx

刚开始，🌴该项目实现了一个小型的web服务器对涉及到普通HTTP请求需要规范消息数据的解析流程与方式。

借此本项目只涉及下面常用消息体类型进行解析，但只能进行简单数据类型进行解析，涉及到复杂数据尚不支持（文件流、引用类型（数组））。

🍁后面进行自定义Spring Starter 就能支持复杂类型了，因为只需要规定数据传输的类型格式和数据封装进行发起HTTP请求，数据由Spring MVC进行解析并返回就行，但是这样会造成重试机制的把控度有所折损。

实现细节：

- 初始化环境变量
- 向注册中心注入服务信息
- 初始化Web容器
- 扫描包路径
- 绑定请求接口
- 请求体数据处理和封装（对rpc调用和http普通请求分别处理）
- 操作远程调用服务类字节码，生成远程调用代理对象
- 注入远程调用代理对象
- 对远程请求数据进行封装
- 远程请求策略（心跳检测，重试机制，负载均衡。。。。）
- 封装响应信息，返回调用者

> 这里自定义了简单的Web容器，为了让项目有扩展性，需要直接进行数据解析和封装，就要搞清
>
> Content-Type（Verxt）  常用请求数据类型 对应 数据 保存在哪里

**每个请求类型（GET、POST、PUT、DELETE）传数据都能成功**，需要规定请求方式的数据处理规范，简化处理数据流程链，下面数据的格式和消息数据存放位置。

`none`：param、query

~~~sh
query: name=renjia&zyh=love
param: key-value 
~~~

`application/x-www-form-urlencoded`：body

~~~shell
name=rejia
~~~

`multipart/form-data`：body

~~~sh
----------------------------876502053268893838035583
Content-Disposition: form-data; name="name"

34
----------------------------876502053268893838035583
Content-Disposition: form-data; name="dddd"

fjak
----------------------------876502053268893838035583--
~~~

`application/json`：body

~~~json
{
    "name":"renjia"
}
~~~

### 🍑 GET、DELETE

> 规范：query、application/x-www-form-urlencoded
>
> Controler层：方法参数可以有多个，参数名要和前端传递的参数名一致

### 🍅 PUT、POST

> 规范：multipart/form-data、application/json
>
> Controler层：
>
> multipart/form-data：方法参数只能有一个，方便反序列化处理
>
> application/json：方法参数可以有多个，参数名要和前端传递的参数名一致



## 🍈 项目概要

maven构建：https://blog.csdn.net/tangjieqing/article/details/145964631

~~~
#项目中有一些测试例子，必须按一些步骤来构建项目，否则运行过程中或处理请求是会报错
1. 启动项目之前，双击`etcd.exe`(windows)，启动etcd服务器
2. 先把被依赖的资源 clear->install
3. 自己的Maven项目再 clear->install
4. 启动项目
~~~

### 🍉 Vertx

#### 1： Maven导包

~~~xml
<dependency>
            <groupId>org.example</groupId>
            <artifactId>rpc_core</artifactId>
            <version>1.0-SNAPSHOT</version>
</dependency>
~~~

#### 2： 配置文件（rpc.config.properties）信息


~~~properties
# rpc.config.properties
# vertx web服务端口（唯一）
ri.rpc.port=8026
# controller包
ri.rpc.provisionScanPackge=com.renjia.producer.controller
# 调用远程
ri.rpc.remoteScanPackge=com.renjia.producer.fetch
# 是否注册自己
ri.rpc.registe.enable=false
# 注册服务名（唯一）
ri.rpc.registe.serverName=producer
# 注册中心类型
rj.rpc.registe.registerType=ETCD
# 注册中心地址
rj.rpc.registe.enpoint=http://127.0.0.1:2379
# 服务分组
rj.rpc.registe.registePrefix=/rj/rpc/
# 消息反序列化类型
rj.rpc.registe.serializerType=FASTJSON
~~~

#### 3： 自定义启动类

相关自定义启动类参考：`com.renjia.rpc.core.VertxServer`

~~~
public abstract class StorageRegisterInfo {

    //初始化容器
    protected abstract void init();

    //启动容器
    protected abstract void start();

    //启动注册器
    protected abstract void startRegister();

    //初始化服务提供者
    protected abstract void serviceProvisionInit();

    //是否是Vertx web 环境
    protected abstract Boolean isVertxEnvironment();


}
~~~

#### 4：注解说明

- `@RpcFetch`：表示该接口是一个远程调用服务类，类似与OpenFeign中的`@FeginClient`
- `@RequestInfo`：表示该方法是一个远程暴露的调用，相当于使用OpenFeign是接口方法上使用的Spring MVC`@RequestMapping`
- `@RpcExposure`：修饰类表示该类使用个服务提供者(类似与Spring MVC中的`@Controller`)，修饰类属性表示该属性是一个代理暴露类(Spring 中的`@AutoWrited`)。
- `@ExposePoint`：修饰方法表示该方法是一个处理请求方法，类似与Spring MVC中的`@RequestMapping`
- `@ExposeParam`：修饰方法属性类似与Spring MVC中的`@RequestParam`



### 🌽 Spring Starter（完成）

#### 1： Maven导包

~~~xml
<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-parent</artifactId>
        <version>2.3.12.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>com.renjia.sourcecode_java</groupId>
            <artifactId>rpc-rjmeteor-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
     <!--    排除了上层，解决 ClassNotFoundException: io.netty.handler.logging.ByteBufFormat冲突        -->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework</groupId>
                    <artifactId>spring-web</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <!--   解决DefaultParameterNameDiscoverer获取方法参数名为空的问题         -->
                    <parameters>true</parameters>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
~~~

#### 2： 配置文件（application.yml）信息

~~~yml
server:
  port: 8081
rpc:
  registe:
    enable: true  #是否注册自己
    server-name: producer #服务名（全场唯一）
~~~

#### 3: 自动配置

- `@EnableRPCAutoServer`：向Bean容器注册接口的BeanDefiniton
- `ProxyRpcFactoryBean`：代理远程调用接口
- `InitRpcResource`：初始化环境变量和注册中心
- `META-INF/spring.factories`：自动化配置

#### 4：实现细节

- 向Bean容器注册接口的BeanDefiniton
- 代理对象注入Bean容器
- Spring应用启动完，初始化环境变量和注册中心
- 注入远程调用代理对象
- 对远程请求数据进行封装
- 远程请求策略（心跳检测，重试机制，负载均衡。。。。）
- 封装响应信息，返回调用者





