//package com.github.winggao.kt.ppd
//
//import com.dianping.cat.Cat
//import com.github.winggao.kt.request.WError
//import com.ppdai.xygo.iot.core.IoTError
//import com.ppdai.xygo.iot.core.completeSuccess
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.asCoroutineDispatcher
//import kotlinx.coroutines.launch
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import java.util.concurrent.Executors
//import java.util.concurrent.ThreadFactory
//import java.util.concurrent.ThreadPoolExecutor
//import java.util.concurrent.atomic.AtomicInteger
//
///**
// * 可监控的线程池
// */
//class IoTPool(private val nThread: Int, private val namePrefix: String) {
//    private var logger: Logger? = LoggerFactory.getLogger(this.javaClass)
//    private val pool = Executors.newFixedThreadPool(nThread, IoTFactory(namePrefix)) as ThreadPoolExecutor
//    private val scope = CoroutineScope(pool.asCoroutineDispatcher())
//
//    init {
//        if (allPools.containsKey(namePrefix)) throw WError("IoTPool 已存在 ${namePrefix}")
//        allPools.put(namePrefix, this)
//    }
//
//    fun disableLog(): IoTPool {
//        logger = null
//        return this
//    }
//
//    fun launch(eventName: String, block: suspend CoroutineScope.() -> Unit): Job {
//        val tag = "launch_$eventName"
//        logger?.info("$tag call")
//        return scope.launch {
//            val t = Cat.getProducer().newTransaction("ipool-${namePrefix}", eventName)
//            try {
//                logger?.info("$tag start")
//                block(this)
//            } catch (e: Exception) {
//                logger?.error(tag, e)
//            }
//            logger?.info("$tag end")
//            t.completeSuccess()
//        }
//    }
//
//    fun getPoolInfo(): PoolInfo {
//        return PoolInfo(namePrefix, pool)
//    }
//
//
//    private class IoTFactory(val namePrefix: String) : ThreadFactory {
//        private val group: ThreadGroup
//        private val threadNumber = AtomicInteger(1)
//
//
//        init {
//            val s = System.getSecurityManager()
//            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
//        }
//
//        override fun newThread(r: Runnable): Thread {
//            val t = Thread(group, r, "ipool-${namePrefix}-${threadNumber.getAndIncrement()}", 0)
//            if (t.isDaemon) t.isDaemon = false
//            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
//            return t
//        }
//    }
//
//
//    class PoolInfo(var name: String, es: ThreadPoolExecutor) {
//        var corePoolSize = es.corePoolSize
//        var largestPoolSize = es.largestPoolSize
//        var maximumPoolSize = es.maximumPoolSize
//        var activeCount = es.activeCount
//        var poolSize = es.poolSize
//        var completedTaskCount = es.completedTaskCount
//        var queueSize = es.queue.size
//    }
//
//    companion object {
//        val allPools = HashMap<String, IoTPool>()
//    }
//}
