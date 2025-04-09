package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
* 专门用于执行异步任务的自定义线程池，可以在其他服务或者组件上使用
* @Async("logTaskExecutor")注解的时候
* 将方法提交到这里
* */


// 告诉系统这里有一些特别的规则
@Configuration
// 告诉主线程这个任务可以不用管了 就像餐厅前台不用管后厨备餐
// 如果没有的话  主线程还是会去处理这些事
@EnableAsync
public class AsyncConfig {
    // 后厨团队名
    @Bean(name = "logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 团队至少要有两名厨师（线程）
        executor.setCorePoolSize(2);
        // 如果订单特别多 可以加到10个厨师
        executor.setMaxPoolSize(10);
        // 最多可以有100个订单 再多了就处理不了了
        executor.setQueueCapacity(100);
        // 每个厨师的名字前面都是log-thread-
        executor.setThreadNamePrefix("log-thread-");
        executor.initialize();
        return executor;
    }
}