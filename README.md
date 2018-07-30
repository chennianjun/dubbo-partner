# dubbo框架

## 1、集群容错
### 容错模式
* Failover Cluster:失败自动切换，当出现失败，重试其他的服务器。通常使用读操作，但重试会带来更长的延时。
#####配置如下：
```xml
<dubbo:service retries="2"/>  
```
或  
```xml
<dubbo:reference retries="2"/>
```
或
```xml
<dubbo:reference>  
    <dubbo:method name="findFoo" retries="2"/>
</dubbo:reference>
```
* Failfast Cluster:快速失败、只发起一次请求、失败立即报错。通常用于非幂等性的操作、如新增
* Failsafe Cluster:失败安全，出现异常、直接忽略，通常用于写入审计日志等操作
* Failback Cluster:失败恢复、后台记录失败请求，定时重发。通常用于消息通知操作。
* Forking Cluster:并行调用多台服务器，只要有一个成功即可返回，用于读实时较高的操作，可通过forks="2"来设置并行性
* Broadcast Cluster:广播调用所有提供者，逐个调用，任意一台报错则报错

### 集群模式配置
```xml
<dubbo:service cluster="failsafe"/>
```
或
```xml
<dubbo:reference cluster="failsafe"/>
```
## 2、负载均衡
### 负载均衡策略
> * Random LoadBalance
>> 随机,按权重设置随机概率。
> * RoundRobin LoadBalance
>> 轮循，按公约后的权重设置轮循比率。
> * LeastActive LoadBalance
>> 最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
> * ConsistentHash LoadBalance
>> 一致性 Hash，相同参数的请求总是发到同一提供者。  
>> 缺省只对第一个参数 Hash，如果要修改，如下配置:
>> `<dubbo:parameter key="hash.arguments" value="0,1" />`  
>> 缺省用 160 份虚拟节点，如果要修改,如下配置:
`<dubbo:parameter key="hash.nodes" value="320" />`
### 负载均衡配置

#### 服务端服务级别
```xml
<dubbo:service interface="..." loadbalance="roundrobin"/>
```
#### 客户端服务级别
```xml
<dubbo:reference interface="..." loadbalance="roundrobin"/>
```

#### 服务端方法级别
```xml
<dubbo:service interface="...">
    <dubbo:method name="findFoo" loadbalance="roundrobin"/>
</dubbo:service>
```

#### 客户端方法级别
```xml
<dubbo:reference interface="...">
    <dubbo:method name="findFoo" loadbalance="roundrobin"/>
</dubbo:reference>
```
## 3、直连提供者
```
  注意：该模式只建议在测试环境使用，绕过注册中心直接与服务提供之进行连接。
```
### XML配置
```XML
<dubbo:reference id="xxxService" interface="..." url="dubbo://localhost:20890"/>
```
### 通过 -D 参数指定
```
java -Dcom.alibaba.xxx.XxxService=dubbo://localhost:20890
```
### 通过文件映射
服务较多的情况下，也可以使用文件映射用`-Ddubbo.resolve.file`此配置优先级高于`<dubbo:reference>`  
`java -Ddubbo.resolve.file=xxx.properties`  
然后在映射文件 xxx.properties 中加入配置，其中 key 为服务名，value 为服务提供者 URL：  
`com.alibaba.xxx.XxxService=dubbo://localhost:20890`

## 3、只订阅
![Sub](images/subscribe-only.jpg)  
##### 禁止注册服务
`<dubbo:registry address="ip:port" register="false"/>`
##### 或者
`<dubbo:registry address="ip:port?register=false"/>`
## 4、只注册服务
*如果有两个镜像环境，两个注册中心，有一个服务只在其中一个注册中心有部署，另一个注册中心还没来得及部署，而两个注册中心的其它应用都需要依赖此服务。这个时候，可以让服务提供者方只注册服务到另一注册中心，而不从另一注册中心订阅服务.*

#### 禁止订阅配置
```
  <dubbo:registry address="ip:port" id="xxxService"/>
  <dubbo:registry address="ip:port" id="xxxService" subscribe="false"/>
```
#### 或者
```
  <dubbo:registry address="ip:port" id="xxxService"/>
  <dubbo:registry address="ip:port?subscribe=false" id="xxxService"/>
```

## 5、静态服务
*有时候希望人工管理服务提供者的上线和下线，此时需将注册中心标识为非动态管理模式。*  
`<dubbo:registry address="ip:port" dynamic="false"/>`  
*或者*  
`<dubbo:registry address="ip:port?dynamic=false"/>`
```
 注意：该情景下，注册中心不会自动删除，只能手动删除
```
*如果是一个第三方独立提供者，比如 memcached，可以直接向注册中心写入提供者地址信息，消费者正常使用 .*  
```java
RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
Registry registry = registryFactory.getRegistry(URL.valueOf("zookeeper://10.20.153.10:2181"));
registry.register(URL.valueOf("memcached://10.20.153.11/com.foo.BarService?category=providers&dynamic=false&application=foo"));
```
## 6、多协议
*不同的服务应用不同的协议的场景。*
#### xml配置如下：
```XML
    <dubbo:application name="Multi-Protocol-Provider"/>

    <!--配置服务的注册中心-->
    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"/>

    <!-- 多协议配置,配置dubbo协议-->
    <dubbo:protocol name="dubbo" port="20880"/>

    <!--配置rmi协议-->
    <dubbo:protocol name="rmi" port="1099"/>

    <!-- 使用dubbo协议暴露服务 -->
    <dubbo:service interface="com.base.dubbo.service.DemoService" version="1.0" ref="demoService" protocol="dubbo"/>
    <!-- 使用rmi协议暴露服务 -->
    <dubbo:service interface="com.base.dubbo.service.AddService" version="1.0" ref="addService" protocol="rmi"/>
```
*同一个服务绑定不同的协议。*
#### xml配置如下：
```XML
  <dubbo:application name="Multi-Protocol-Provider"/>

  <!--配置服务的注册中心-->
  <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"/>

  <!-- 多协议配置，配置dubbo协议-->
  <dubbo:protocol name="dubbo" port="20880"/>

  <!--配置rmi协议-->
  <dubbo:protocol name="rmi" port="1099"/>

  <!-- 使用dubbo协议暴露服务 -->
  <!--<dubbo:service interface="com.base.dubbo.service.DemoService" version="1.0" ref="demoService" protocol="dubbo"/>-->
  <!-- 使用rmi协议暴露服务 -->
  <dubbo:service interface="com.base.dubbo.service.DemoService" ref="demoService" protocol="dubbo,rmi"/>
```
## 7、多注册中心
*同一个服务注册到不同的注册中心。*
#### xml配置：
```XML
    <dubbo:application name="multi-registry-provider"/>

    <!-- 多注册中心配置 -->
    <dubbo:registry id="chain" protocol="zookeeper" address="127.0.0.1:2181"/>
    <dubbo:registry id="us" protocol="zookeeper" address="127.0.0.1:2182"/>

    <!--多协议配置-->
    <dubbo:protocol name="dubbo" port="20881"/>
    <dubbo:protocol name="rmi" port="1099"/>

    <dubbo:service interface="com.base.dubbo.service.DemoService" ref="demoService" protocol="dubbo,rmi" registry="chain,us"/>
```
*不同服务不同注册中心。*
#### xml配置：
```XML
    <dubbo:application name="multi-registry-provider"/>

    <!-- 多注册中心配置 -->
    <dubbo:registry id="chain" protocol="zookeeper" address="127.0.0.1:2181"/>
    <dubbo:registry id="us" protocol="zookeeper" address="127.0.0.1:2182"/>

    <!--多协议配置-->
    <dubbo:protocol name="dubbo" port="20881"/>
    <dubbo:protocol name="rmi" port="1099"/>

    <dubbo:service interface="com.base.dubbo.service.DemoService" ref="demoService" protocol="dubbo,rmi" registry="chain"/>
    <dubbo:service interface="com.base.dubbo.service.AddService" ref="addService" protocol="dubbo,rmi" registry="us"/>
```
*多注册中的引用。*
#### xml配置：
```XML
    <dubbo:application name="multi-registry-consumer"/>
    <!--多注册中心配置-->
    <dubbo:registry id="us" protocol="zookeeper" address="127.0.0.1:2182"/>
    <dubbo:registry id="chain" protocol="zookeeper" address="127.0.0.1:2181"/>

    <!--多协议配置-->
    <dubbo:protocol name="dubbo" port="20881"/>
    <dubbo:protocol name="rmi" port="1099"/>

    <dubbo:reference id="demoService" interface="com.base.dubbo.service.DemoService" protocol="dubbo" registry="chain"/>
    <dubbo:reference id="addService" interface="com.base.dubbo.service.AddService" protocol="dubbo" registry="us"/>
```
*如果只是测试环境临时需要连接两个不同注册中心，使用竖号分隔多个不同注册中心地址：*
```XML
    <dubbo:application name="multi-registry-consumer"/>
    <!-- 多注册中心配置，竖号分隔表示同时连接多个不同注册中心，同一注册中心的多个集群地址用逗号分隔 -->
    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2182|127.0.0.1:2181" />
    <!-- 引用服务 -->
    <dubbo:reference id="helloService" interface="com.alibaba.hello.api.HelloService" version="1.0.0" />
```
## 8、服务分组
*使用场景:当一个接口有多种实现时，可以用 group 区分。*
#### 服务提供者
```XML
<dubbo:service group="feedback" interface="com.xxx.IndexService" />
<dubbo:service group="member" interface="com.xxx.IndexService" />
```
#### 服务消费者
```XML
<dubbo:reference id="feedbackIndexService" group="feedback" interface="com.xxx.IndexService" />
<dubbo:reference id="memberIndexService" group="member" interface="com.xxx.IndexService" />
```
##### 通用组
```XML
<dubbo:reference id="barService" interface="com.foo.BarService" group="*"/>
```
## 9、多版本
*当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。*
