# [dubbo框架API](http://dubbo.apache.org)
### ** [dubbo框架源码](https://github.com/apache/incubator-dubbo) **

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
比如：老版本提供者配置为`<dubbo:service interface="com.base.barService" version="1.0.0"/>`新版本提供者服务可以配置`<dubbo:service interface="com.base.barService" version="2.0.0"/>`,新老版本不会相互调用。
则老版本的调用方配置`<dubbo:reference id="barService" interface="com.base.barService" version="1.0.0"/>`,新版本的调用者则`<dubbo:reference id="barService" interface="com.base.barService" version="2.0.0"/>`.如果不区分新老版本的调用则可这样配置:`<dubbo:reference id="barService" interface="com.base.barService" version="*"/>`
## 10、分组聚合
#### 配置
- *搜索所有分组*
```XML
<dubbo:reference id="demoService" interface="com.base.dubbo.service.DemoService" group="*" merger="true"/>
```
- *合并指定的方法*
```XML
    <dubbo:reference id="demoService2" interface="com.base.dubbo.service.DemoService" group="*">
        <dubbo:method name="getList" merger="true"/>
    </dubbo:reference>
```
- *合并指定的分组*
```xml
    <dubbo:reference id="demoService3" interface="com.base.dubbo.service.DemoService" group="merge-1,merge-2" merger="true"/>
```
- *指定合并策略，缺省根据返回值自动匹配,如果同一类型有两个合并器时，需指定合并器的名称,合并策略需要实现`com.alibaba.dubbo.rpc.cluster.Merger`这个类中的`merger`方法*
```XML
    <dubbo:reference id="demoService4" interface="com.base.dubbo.service.DemoService" group="*">
        <dubbo:method name="getList" merger="myMerge"/>
    </dubbo:reference>
```
- *指定合并的方法将调用返回结果的指定方法进行合并，合并方法的参数类型必须是返回结果类型本身*
```XML
    <dubbo:reference id="demoService5" interface="com.base.dubbo.service.DemoService" group="*">
        <dubbo:method name="getList" merger="myMerge"/>
    </dubbo:reference>
```
## 11、参数验证
#### Mavne依赖
```XML
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>1.0.0.GA</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>4.2.0.Final</version>
</dependency>
```
#### 代码示例
```java
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo.api
 * @create_date:2018/1/23 10:40
 * @author:Subtimental
 * @description:TODO
 */
public class ValidationParameter implements Serializable{
    @NotNull // disallow null
    @Size(min = 2, max = 20) // min and max
    private String name;

    @NotNull(groups = ValidationService.Save.class) // disallow null when save, but allow null when update, that is: not update
    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
    private String email;

    @Min(18) // min
    @Max(100) // max
    private int age;

    @Past // must be a past time
    private Date loginDate;

    @Future // must be a future time
    private Date expiryDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
```
#### 分组和关联验证示例
```java
import javax.validation.GroupSequence;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo.api
 * @create_date:2018/1/23 10:41
 * @author:Subtimental
 * @description:TODO
 */
public interface ValidationService {
    void save(ValidationParameter parameter);

    void update(ValidationParameter parameter);

    void delete(@Min(1) long id, @NotNull @Size(min = 2, max = 16) @Pattern(regexp = "^[a-zA-Z]+$") String operator);

    /**
     * annotation which has the same name with the method but has the first letter in capital
     * used for distinguish validation scenario, for example: @NotNull(groups = ValidationService.Save.class)
     * optional
     */
    @GroupSequence(Update.class)// 同时验证Update组规则
    @interface Save {
    }

    /**
     * annotation which has the same name with the method but has the first letter in capital
     * used for distinguish validation scenario, for example: @NotNull(groups = ValidationService.Update.class)
     * optional
     */
    @interface Update {
    }
}
```
#### 提供者配置文件
```XML
    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="hello-world-app"/>

    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181" check="false"/>

    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo"  port="20881" />

    <!-- 和本地bean一样实现服务 -->
    <dubbo:service interface="com.base.dubbo.api.ValidationService" ref="validationService" validation="true" />

    <bean id="validationService" class="com.base.dubbo.service.ValidationServiceImpl"/>
```
#### 消费方配置文件
```XML
    <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
    <dubbo:application name="consumer-of-helloworld-app"  />

    <!-- 使用multicast广播注册中心暴露发现服务地址 -->
    <dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"/>

    <!-- 生成远程服务代理，可以和本地bean一样使用demoService -->
    <!--dubbo.reference.check="false"属性是关闭某个服务的启动时检查-->
    <!--dubbo.consumer.check="false"属性是关闭所有服务的启动时检查,相当于配置默认值-->
    <!--dubbo.registry.check="false"属性是关闭注册中心启动时检查-->
    <dubbo:reference id="validationService" interface="com.base.dubbo.api.ValidationService"
                     validation="true"/>
```
#### 消费方验证异常信息
```java
import com.base.dubbo.api.ValidationParameter;
import com.base.dubbo.api.ValidationService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Set;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo
 * @create_date:2018/1/23 10:51
 * @author:Subtimental
 * @description:TODO
 */
public class ValidationConsumer {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();

        ValidationService validationService = (ValidationService) context.getBean("validationService");

        // Save OK
        ValidationParameter parameter = new ValidationParameter();
        parameter.setName("liangfei");
        parameter.setEmail("liangfei@liang.fei");
        parameter.setAge(50);
        parameter.setLoginDate(new Date(System.currentTimeMillis() - 1000000));
        parameter.setExpiryDate(new Date(System.currentTimeMillis() + 1000000));
        validationService.save(parameter);
        System.out.println("Validation Save OK");

        // Save Error
        try {
            parameter = new ValidationParameter();
            validationService.save(parameter);
            System.err.println("Validation Save ERROR");
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            System.out.println(constraintViolations);
        }

        // Delete OK
        validationService.delete(2, "abc");
        System.out.println("Validation Delete OK");

        // Delete Error
        try {
            validationService.delete(0, "abc");
            System.err.println("Validation Delete ERROR");
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            System.out.println(constraintViolations);
        }
    }

}
```
## 12、结果缓存
*结果缓存 ，用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量 。*
#### 缓存类型
- `lru`:基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
- `threadlocal`:当前线程缓存，比如一个页面渲染，用到很多`portal`，每个`portal`都要去查用户信息，通过线程缓存，可以减少这种多余访问。
- `jcache` 与 `JSR107` 集成，可以桥接各种缓存实现。
#### 消费端配置
```XML
<dubbo:reference interface="XXX" cache="lru"/>
```
#### 或者
```XML
<dubbo:reference interface="xxx">
    <dubbo:method name="findBar" cache="lru"/>
<dubbo:reference/>
```
## 13、泛化引用
###`与泛化的实现刚好相反，在客户端进行调用没有api的情况`
*泛化接口调用方式主要用于客户端没有 `API` 接口及模型类元的情况，参数及返回值中的所有 `POJO` 均用 `Map` 表示，通常用于框架集成，比如：实现一个通用的服务测试框架，可通过 `GenericService` 调用所有服务实现。*
#### 客户端泛型引用配置
```XML
<dubbo:reference id="demoService" interface="com.base.dubbo.service.DemoService" generic="true"/>
```
#### 或者java配置
```java
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.base.dubbo.service.DemoService;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
    public static void main(String[] args) throws Exception {
        //使用java配置
        // 引用远程服务
        // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
        ReferenceConfig<GenericService> serviceReferenceConfig = new ReferenceConfig<>();
        //配置应用相关信息
        ApplicationConfig applicationConfig = new ApplicationConfig("genericName");
        serviceReferenceConfig.setApplication(applicationConfig);
        serviceReferenceConfig.setInterface(DemoService.class);
        //配置注册信息
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("127.0.0.1:2181");
        registryConfig.setProtocol("zookeeper");
        serviceReferenceConfig.setRegistry(registryConfig);
        // 声明为泛化接口
        serviceReferenceConfig.setGeneric(true);
        // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用
        GenericService genericService = serviceReferenceConfig.get();
        // 基本类型以及Date,List,Map等不需要转换，直接调用
        Object sayHello = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"generic"});
        System.out.println(sayHello);

        // 用Map表示POJO参数，如果返回值为POJO也将自动转成Map
        Map<String, Object> person = new HashMap<>();
        person.put("id", "007");
        person.put("name", "zhangsan");
        // 如果返回POJO将自动转成Map
        Object findByPerson = genericService.$invoke("findByPerson", new String[]{"com.base.dubbo.domain.Person"}, new Object[]{person});
        System.out.println(findByPerson);
    }
}
```
## 14、泛化的实现
###`与泛化引用刚好相反，在服务端进行实现没有api的情况`
*泛接口实现方式主要用于服务器端没有API接口及模型类元的情况，参数及返回值中的所有POJO均用Map表示，通常用于框架集成，比如：实现一个通用的远程服务Mock框架，可通过实现GenericService接口处理所有服务请求。*
