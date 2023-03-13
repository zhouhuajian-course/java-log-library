package org.example;

// 不会注意到logback的存在
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // 参数 logger name
        Logger logger = LoggerFactory.getLogger(App.class);

        // System.out.println( "Hello World!" );
        // class ch.qos.logback.classic.Logger
        // logger 会变成具体实现框架的 Logger 类对象
        // System.out.println(logger.getClass());

        logger.info("Hello World!");
    }
}
