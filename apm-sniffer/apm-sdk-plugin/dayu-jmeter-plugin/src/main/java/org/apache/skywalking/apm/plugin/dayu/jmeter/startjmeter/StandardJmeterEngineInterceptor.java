package org.apache.skywalking.apm.plugin.dayu.jmeter.startjmeter;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.dayu.jmeter.common.TaskIdCache;
import org.apache.skywalking.apm.plugin.dayu.jmeter.httphc4.DayuJmeterHc4Interceptor;
import org.apache.skywalking.apm.plugin.dayu.jmeter.utils.SystemUtil;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;

public class StandardJmeterEngineInterceptor implements InstanceMethodsAroundInterceptor , InstanceConstructorInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(StandardJmeterEngineInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        LOGGER.info("StandardJmeterEngine-getTaskId Interceptor success");
        LOGGER.info("StandardJmeterEngine-getTaskId result : 【{}】", ret);
        if(ret != null){
            TaskIdCache.TASK_ID = (int) ret;
        }
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
    }

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {

    }
}
