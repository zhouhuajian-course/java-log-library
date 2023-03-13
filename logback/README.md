# Logback

## source repository

https://github.com/qos-ch/logback

## slf4j 如何绑定(bind) 具体实现框架 logback

1. org.slf4j.LoggerFactory.getLogger(java.lang.String)
    ```text
    public static Logger getLogger(String name) {
       ILoggerFactory iLoggerFactory = getILoggerFactory();
       return iLoggerFactory.getLogger(name);
    }
    ```
2. org.slf4j.LoggerFactory.getILoggerFactory
    ```text
    public static ILoggerFactory getILoggerFactory() {
        return getProvider().getLoggerFactory();
    }
    ```
3. org.slf4j.LoggerFactory.getProvider
4. org.slf4j.LoggerFactory.performInitialization
5. org.slf4j.LoggerFactory.bind
6. org.slf4j.LoggerFactory.findServiceProviders
   这步是关键，找所有的SLF4J服务/业务提供者，查找classpath下的所有 实现 SLF4JServiceProvider 的类，
   生成一个ArrayList providersList size = 1
          0 = {LogbackServiceProvider@929}
    ```text
    static List<SLF4JServiceProvider> findServiceProviders() {
        ClassLoader classLoaderOfLoggerFactory = LoggerFactory.class.getClassLoader();
        ServiceLoader<SLF4JServiceProvider> serviceLoader = getServiceLoader(classLoaderOfLoggerFactory);
        List<SLF4JServiceProvider> providerList = new ArrayList();
        Iterator<SLF4JServiceProvider> iterator = serviceLoader.iterator();

        while(iterator.hasNext()) {
            safelyInstantiate(providerList, iterator);
        }

        return providerList;
    }
    ```
7. ```text
    List<SLF4JServiceProvider> providersList = findServiceProviders();
    reportMultipleBindingAmbiguity(providersList);
    // 提供者列表不为null并且不为空
    if (providersList != null && !providersList.isEmpty()) {
        // 获取第一个提供者
        PROVIDER = (SLF4JServiceProvider)providersList.get(0);
        // 提供者初始化
        PROVIDER.initialize();
        // 初始化状态 3
        INITIALIZATION_STATE = 3;
        reportActualBinding(providersList);
    } else {
        INITIALIZATION_STATE = 4;
        Util.report("No SLF4J providers were found.");
        Util.report("Defaulting to no-operation (NOP) logger implementation");
        Util.report("See https://www.slf4j.org/codes.html#noProviders for further details.");
        Set<URL> staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
        reportIgnoredStaticLoggerBinders(staticLoggerBinderPathSet);
    }
    ```
8. ch.qos.logback.classic.spi.LogbackServiceProvider.initialize
9. ch.qos.logback.classic.spi.LogbackServiceProvider.getLoggerFactory

   返回默认的Logger上下文，实现ILoggerFactory

   `this.defaultLoggerContext = new LoggerContext();`

   `public class LoggerContext extends ContextBase implements ILoggerFactory, LifeCycle {`

    ```text
     public ILoggerFactory getLoggerFactory() {
         return defaultLoggerContext;
     
         //  if (!initialized) {
         //      return defaultLoggerContext;
         //  
     
         //  if (contextSelectorBinder.getContextSelector() == null) {
         //      throw new IllegalStateException("contextSelector cannot be null. See also " + NULL_CS_URL);
         //  }
         //  return contextSelectorBinder.getContextSelector().getLoggerContext();
     }
     ```
10. ch.qos.logback.classic.LoggerContext.getLogger(java.lang.String)

最重要的类是 LoggerFactory，Logger是接口，最终获取到的 logger 是具体框架的 Logger 类对象

结论：在具体实现框架中提供实现了SLF4JServiceProvider的类，例如logback的LogbackServiceProvider，
SLF4J会生成LogbackServiceProvider的实例，调用其initialize方法，然后调用getLoggerFactory获取真正的Logback里面的日志工厂，
也就是LoggerContext
最后调用Logback日志工厂LoggerContext里面的getLogger获取具体的日志实例。

## slf4j 源码中的 I 前缀

可能表示 Interface，当一个类是 Interface 的时候，前缀 I 可加可不加

slf4j的ILoggerFactory可能是为了和LoggerFactory进行区分，避免重名

## synchronous and asynchronous logging

同步 和 异步 日志

Log4j 1.2 as well as logback have supported asynchronous logging for many years by the way of AsyncAppender. This appender essentially collects newly created logging events, as produced by the application, into a circular buffer. The events in this circular buffer are then processed by a dedicated worker thread which writes the events to their destination, be it a file, a remote server or a database.

## logback version

**STABLE version (ACTIVELY DEVELOPED)**

1.3.5 支持 Java EE 需要 SLF4J 2.0.4 和 JDK 8  
The current actively developed version of logback supporting Java EE (java.* namespace) is 1.3.5. It requires SLF4J version 2.0.4 and JDK 8.

1.4.5 支持 Jakarta EE 需要 SLF4J 2.0.4 和 JDK 11  
The current actively developed version of logback supporting Jakarta EE (jakarta.* namespace) is 1.4.5. It requires SLF4J version 2.0.4 and JDK 11.

**Older stable version (INACTIVE)**

1.2.11 比较老的稳定版本  
The older stable logback version is 1.2.11.

## logback.qos.ch

ch 瑞士域名后缀 qos 公司名 

Switzerland

## three modules

logback's architecture is quite generic so as to apply under different circumstances. At present time, logback is divided into three modules, logback-core, logback-classic and logback-access.

The logback-core module lays the groundwork for the other two modules. The logback-classic module can be assimilated to a significantly improved version of log4j 1.x. Moreover, logback-classic natively implements the SLF4J API so that you can readily switch back and forth between logback and other logging frameworks such as log4j 1.x or java.util.logging (JUL).

The logback-access module integrates with Servlet containers, such as Tomcat and Jetty, to provide HTTP-access log functionality. Note that you could easily build your own module on top of logback-core.

logback-core 核心模块 其他模块的日志记录底层实现

## translation services 转换服务

List of online services (requiring authentication)
Please note in order to use the following services, you will need to authenticate yourself via Github.

Properties translator  属性转化器 将log4j.properties文件转成logback.xml配置文件

This page allows you to translate a log4j.properties file into a logback.xml configuration file. The resulting logback.xml file targets logback version 1.2.0 or later. Note that both logback 1.3 and 1.4 support 1.2 configuration files without change.

Canonize logback.xml  规范 logback.xml 转换 logback.xml文件 为更加规范的格式

This service allows you to transform logback.xml files into canonical or standard form. The resulting logback.xml file targets logback version 1.3.0 or later. Note that both logback 1.3 and 1.4 support 1.2 configuration files without change.

## third-party tools 第三方工具

## articles and presentations 文章和代表文章

A Guide to Logback by Eric Goebelbecker  
Solving Your Logging Problems with Logback by Eugen Paraschiv  
Migrating off of Log4j 2.x by Thomas Broyer  
Jetty/Tutorial/Sifting Logs with Logback by Shirly Dekker Boulay  
Enterprise Spring Best Practices by Gordon Dickens  
Logback project, by Ceki Gülcü and Sébastien Pennec.  
Logback: Evolving Java Logging by Geoffrey Wiseman  
Logging in OSGI Enterprise Applications, by Ekkehard Gentz  

## performance

![performance-01.png](performance-01.png)

![performance-02.png](performance-02.png)

## 工作线程 worker thread

a dedicated worker thread 一个专用的工作线程

## 架构、设计原理、创始人

architecture, design rationale, founder

## requires the presence of slf4j-api.jar and logback-core.jar

Logback-classic module requires the presence of slf4j-api.jar and logback-core.jar in addition to logback-classic.jar on the classpath.

## 绝大多少情况 使用的都是 slf4j 的 类，不会注意到 logback 的存在

slf4j Logger和LoggerFactory

Note that the above example does not reference any logback classes. In most cases, as far as logging is concerned, your classes will only need to import SLF4J classes. Thus, the vast majority, if not all, of your classes will use the SLF4J API and will be oblivious to the existence of logback.

注意，上面的示例没有引用任何logback类。在大多数情况下，就日志记录而言，您的类只需要导入SLF4J类。因此，绝大多数(如果不是全部的话)类将使用SLF4J API，并且不会注意到logback的存在。

## 默认 Appender

By virtue of logback's default configuration policy, when no default configuration file is found, logback will add a ConsoleAppender to the root logger.

没有默认配置文件情况下，logback会加一个 ConsoleAppender 到 根 logger (root logger)

## logback-core

抽离出 logback-core 是为了让代码更好地复用

