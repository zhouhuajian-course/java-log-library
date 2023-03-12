# logback

## source repository

https://github.com/qos-ch/logback

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
