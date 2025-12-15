package com.itzixi;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @ClassName ServiceLogAspect
 * @Author 陈壮壮
 * @Version 1.0
 * @Description ServiceLogAspect
 **/
@Slf4j
@Component
@Aspect
public class ServiceLogAspect {

    /**
     * @Description: 切面表达式
     *              *       返回任意类型，比如 void，object，list 等
     *              com.itzixi.service.impl  指定包名，要去具体切入切面的位置（某个java class所在的包位置）
     *              ..      可以匹配到当前包以及它的子包
     *              *       可以匹配当前包和子包下的java class
     *              .       无意义
     *              *       代表任意的方法名
     *              (..)    代表方法名的参数，这个参数是可以被传入的，也可以无参数
     * @Author 陈壮壮
     * @param
     * @return Object
     */
    @Around("execution(* com.itzixi.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();
//        long begin = System.currentTimeMillis();
        stopWatch.start();

        Object proceed = joinPoint.proceed();
        String point = joinPoint.getTarget().getClass().getName()
                        + "." +
                       joinPoint.getSignature().getName();

//        long end = System.currentTimeMillis();

        stopWatch.stop();
        // 获得时间差
//        long takeTime = end - begin;

        // 打印任务的耗时统计
        log.info(stopWatch.prettyPrint());
        log.info(stopWatch.shortSummary());

        // 任务信息总览
        log.info("所有任务的总耗时：" + stopWatch.getTotalTimeMillis());
        log.info("任务总数：" + stopWatch.getTaskCount());

//        log.info("直接方法：{} 执行的时间为 {} 毫秒", point, takeTime);

        return proceed;
    }

}
