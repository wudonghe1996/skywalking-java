package org.apache.skywalking.apm.plugin.dayu.jmeter.httpjava;

import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.*;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;
import org.apache.skywalking.apm.plugin.dayu.jmeter.httphc4.DayuJmeterHc4Interceptor;
import org.apache.skywalking.apm.plugin.dayu.jmeter.utils.SystemUtil;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;

public class DayuJmeterInterceptor implements InstanceMethodsAroundInterceptor , InstanceConstructorInterceptor {
    private static final ILog LOGGER = LogManager.getLogger(DayuJmeterInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        LOGGER.info("start dayu agent HTTPJavaImpl");
        HttpURLConnection conn = (HttpURLConnection) allArguments[0];
        String sw8 = SystemUtil.getSw8();
        conn.addRequestProperty("sw8", sw8);
        allArguments[1] = conn;
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
