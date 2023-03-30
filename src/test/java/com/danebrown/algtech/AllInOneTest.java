package com.danebrown.algtech;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
public class AllInOneTest {

    @Test
    public void allInOneTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //关闭所有日志
        Log4j2Utils.setLogLevel("com.danebrown.algtech","FATAL");
        Class superClass=Class.forName("com.danebrown.algtech.algcomp.AlgCompImpl");
        Reflections reflections = new Reflections("com.danebrown.algtech");
        Set<Class> cls = reflections.getSubTypesOf(superClass);
        for(Class clsItem:cls) {
            try {
                //如果是abstract类，就跳过
                if(Modifier.isAbstract(clsItem.getModifiers())){
                    continue;
                }
                Object clazz = clsItem.getDeclaredConstructor().newInstance();

                Method method = clazz.getClass().getMethod("compare", new Class[]{String.class});
                System.out.println(clsItem.getSimpleName() + "开始测试");
                CompletableFuture standardFuture = CompletableFuture.supplyAsync(()->{
                    try {
                        return method.invoke(clazz, UUID.randomUUID().toString());
                    } catch (IllegalAccessException|InvocationTargetException e) {

                    }finally {
                        return false;
                    }
                });
                standardFuture.get(10, TimeUnit.SECONDS);

                System.out.println(clsItem.getSimpleName() + "测试完毕");
            }catch (Exception ex){
                System.err.println(MessageFormat.format("{0}",ex));
                Assert.fail(clsItem.getTypeName()+ "测试失败");
            }
        }
    }

    public static class Log4j2Utils {
        public static void setLogLevel(String loggerName, String level) {
            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = loggerContext.getConfiguration();
            LoggerConfig loggerConfig = configuration.getLoggerConfig(loggerName);
            loggerConfig.setLevel(org.apache.logging.log4j.Level.valueOf(level));
            loggerContext.updateLoggers(configuration);
        }
    }
}
