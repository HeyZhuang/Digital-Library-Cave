package com.itzixi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

/**
 * @ClassName Test
 * @Author 陈壮壮
 * @Version 1.0
 * @Description Test
 **/
@SpringBootTest
public class MyTest {

    /**
     * @Description: 使用优雅的方式来统计时间 StopWatch
     * @Author 陈壮壮
     * @param
     */
    @Test
    public void testStopWatch() throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("task1");
        Thread.sleep(500);
        stopWatch.stop();

        stopWatch.start("task2");
        Thread.sleep(800);
        stopWatch.stop();

        stopWatch.start("task3");
        Thread.sleep(300);
        stopWatch.stop();

        // 打印任务的耗时统计
        System.out.println(stopWatch.prettyPrint());
        System.out.println(stopWatch.shortSummary());

        // 任务信息总览
        System.out.println("所有任务的总耗时：" + stopWatch.getTotalTimeMillis());
        System.out.println("任务总数：" + stopWatch.getTaskCount());

    }

}
