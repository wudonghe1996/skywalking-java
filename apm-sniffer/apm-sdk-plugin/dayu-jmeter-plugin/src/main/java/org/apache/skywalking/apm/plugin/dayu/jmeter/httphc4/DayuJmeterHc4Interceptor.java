package org.apache.skywalking.apm.plugin.dayu.jmeter.httphc4;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.plugin.dayu.jmeter.utils.SystemUtil;
import org.apache.http.client.methods.HttpRequestBase;


import java.lang.reflect.Method;

public class DayuJmeterHc4Interceptor implements InstanceMethodsAroundInterceptor, InstanceConstructorInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(DayuJmeterHc4Interceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        LOGGER.info("start dayu agent Hc4");
        HttpRequestBase httpRequest = (HttpRequestBase)allArguments[1];
        String sw8 = SystemUtil.getSw8();
        httpRequest.addHeader("sw8", sw8);
        allArguments[1] = httpRequest;
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {

    }
}
