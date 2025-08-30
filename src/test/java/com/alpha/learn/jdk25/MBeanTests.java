package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import java.lang.management.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MBeanTests {

    @Test
    public void testRuntimeMXBean() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        log.info("{}", runtimeMXBean.getName());
        log.info("{}", runtimeMXBean.getStartTime());
        log.info("{}", runtimeMXBean.getSpecName());
        log.info("{}", runtimeMXBean.getSpecVendor());
        log.info("{}", runtimeMXBean.getPid());
        log.info("{}", runtimeMXBean.getInputArguments());
        log.info("{}", runtimeMXBean.isBootClassPathSupported());
        log.info("{}", runtimeMXBean.getUptime());
        log.info("{}", runtimeMXBean.getVmVendor());
        log.info("{}", runtimeMXBean.getVmVersion());
        log.info("{}", runtimeMXBean.getVmName());
        log.info("{}", runtimeMXBean.getManagementSpecVersion());
        log.info("{}", runtimeMXBean.getLibraryPath());
        log.info("{}", runtimeMXBean.getClassPath());
        log.info("{}", runtimeMXBean.getSystemProperties());
    }

    @Test
    public void testPlatformMBeanServer() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        log.info("{}", mBeanServer.getMBeanCount());
        log.info("{}", mBeanServer.getClass());
        log.info("{}", mBeanServer.getDefaultDomain());
        log.info("{}", Arrays.toString(mBeanServer.getDomains()));
        log.info("{}", mBeanServer.getClassLoaderRepository().toString());
    }

    @Test
    public void testThreadMXBean() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        log.info("{}", threadMXBean.getTotalStartedThreadCount());
        log.info("{}", threadMXBean.getPeakThreadCount());
        log.info("{}", threadMXBean.getDaemonThreadCount());
        log.info("{}", threadMXBean.getCurrentThreadUserTime());
        log.info("{}", threadMXBean.getCurrentThreadCpuTime());
        log.info("{}", Arrays.toString(threadMXBean.getAllThreadIds()));
        long[] threadIds = threadMXBean.getAllThreadIds();
        for (long id : threadIds) {
            log.info("{}", id);
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(id);
            log.info("{}", threadInfo.getThreadState());
        }
    }

    @Test
    public void testClassLoadingMXBean() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        log.info("{}", classLoadingMXBean.getLoadedClassCount());
        log.info("{}", classLoadingMXBean.getTotalLoadedClassCount());
        log.info("{}", classLoadingMXBean.getClass());
        log.info("{}", classLoadingMXBean.getUnloadedClassCount());
    }

    @Test
    public void testMemoryMXBean() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        log.info("{}", memoryMXBean.getHeapMemoryUsage());
        log.info("{}", memoryMXBean.getNonHeapMemoryUsage());
        log.info("{}", memoryMXBean.isVerbose());
        log.info("{}", memoryMXBean.getClass());
    }

    @Test
    public void testGarbageCollectorMXBean() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        log.info("{}", garbageCollectorMXBeans.size());
        log.info("{}", garbageCollectorMXBeans.toString());
        for (GarbageCollectorMXBean bean : garbageCollectorMXBeans) {
            log.info("{}", bean.getName());
            log.info("{}", bean.getCollectionCount());
            log.info("{}", bean.getObjectName().toString());
            log.info("{}", Arrays.toString(bean.getMemoryPoolNames()));
        }
    }
}
