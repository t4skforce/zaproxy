package org.zaproxy.zap.ctx;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.zaproxy.zap.db.repository.HistoryRepository;

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

    public static void main(String[] args) throws Exception {
        HistoryRepository legacyTableHistory = getBean(HistoryRepository.class);
        System.out.println(legacyTableHistory
                .findAllHistoryCache(24L, "http://127.0.0.1:3000/styles.css", "GET", "", 1622009866056L,
                        PageRequest.of(0, 1))
                .get(0)
                .getId());
        close();
    }

}
