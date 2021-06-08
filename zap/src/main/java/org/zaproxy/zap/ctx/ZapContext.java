package org.zaproxy.zap.ctx;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ZapContext {

    private static final String CONFIG_PACKAGE = "org.zaproxy.zap.ctx.config";

    private static final AnnotationConfigApplicationContext ctx = getApplicationContext();

    private static AnnotationConfigApplicationContext getApplicationContext() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan(CONFIG_PACKAGE);
        ctx.refresh();
        return ctx;
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return ctx.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return ctx.getBean(name, requiredType);
    }

    public static void close() throws IOException {
        if (ctx != null && ctx.isActive()) {
            ctx.close();
        }
    }
}
