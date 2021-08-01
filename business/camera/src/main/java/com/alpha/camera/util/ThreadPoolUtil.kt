package com.alpha.camera.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadPoolUtil {
    /**
     * 创建只有一条非核心线程、拥有无界任务队列的线程池，用于执行临时且耗时较短的后台任务
     *
     * @return 只有一条线程的线程池
     */
    fun newSingleThreadPool(): ExecutorService {
        return ThreadPoolExecutor(0,
                1,
                1,
                TimeUnit.MINUTES,
                LinkedBlockingDeque())
    }
}