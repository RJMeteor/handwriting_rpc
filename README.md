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
- 自定义Spring Starter（待完成）



## 🌽 下载Etcd

[etcd-io/etcd: Distributed reliable key-value store for the most critical data of a distributed system (github.com)](https://github.com/etcd-io/etcd)



## 🌰 说明

该项目本是进行`TCP服务器`进行数据传输进行`远程调用`，但是会用一个问题，远程和本地要有完全限定类名才能进行调用，为解决这样的问题只要规定数据传输协议传递目标调用Class类型，只要本地有该Class就可以进行反射调用，这就对后面自定义`Spring Starter`就比较麻烦，因为当涉及到简单反射操作类的时候类的属性值会为空，为解决这样的问题可以到类反射的时候只要涉及类中有属性就去Spring 容器中找到对应的Bean进行属性赋值就可以，然后本地进行缓存，下次进行调用就从缓存拿，但还是会有问题是当调用Spring MVC的Controller层的时候就只是执行了自己实例化（也可以Spring Web容器中）进行调用时不会触发Spring MVC处理链的调用，你可以根据`HttpServletRequest`去`RequestMappingHandlerMapping`中得到也是一样的效果。**最后决定实现HTTP请求的方式进行远程调用（请求），方便后面自定义`Spring Starter`。**



🌴该项目实现了一个小型的web服务器对涉及到普通HTTP请求需要规范消息数据的解析流程与方式。

借此本项目只涉及下面常用消息体类型进行解析，但只能进行简单数据类型进行解析，涉及到复杂数据尚不支持（文件流、引用类型（数组））。



🍁后面进行自定义Spring Starter 就能支持复杂类型了，因为只需要规定数据传输的类型格式进行发起HTTP请求，数据由Spring MVC进行解析并返回就行，但是这样会造成重试机制的把控度有所折损。



> Verxt Content-Type  常用请求数据类型 对应 数据 保存在哪里

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



## 🍈 配置文件

> ### vertx

~~~properties
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



> ### Spring Starter（待完成）

~~~properties
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







