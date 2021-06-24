package org.zaproxy.zap.extension.service.impl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.extension.Extension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DefaultExtensionService {

    private final Logger LOG = LogManager.getLogger(DefaultExtensionService.class);

    @Autowired
    private ApplicationContext context;

    /**
     * Gets the {@code Extension} with the given name.
     *
     * @param name the name of the {@code Extension}.
     * @return the {@code Extension} or {@code empty} if not found/enabled.
     * @see #getExtension(Class)
     */
    Optional<Extension> getExtension(String name) {
        try {
            return Optional.of(context.getBean(name, Extension.class)).filter(Extension::isEnabled);
        } catch (BeansException e) {
            return Optional.empty();
        }
    }

    /**
     *
     * @param name
     * @return
     */
    Optional<Extension> getExtensionByClassName(String name) {
        try {
            return getExtension(Class.forName(name).asSubclass(Extension.class)).map(bean -> bean);
        } catch (ClassNotFoundException | ClassCastException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the {@code Extension} with the given class.
     *
     * @param clazz the class of the {@code Extension}
     * @return the {@code Extension} or {@code empty} if not found/enabled.
     */
    public <T extends Extension> Optional<T> getExtension(Class<T> clazz) {
        try {
            return Optional.of(context.getBean(clazz)).filter(Extension::isEnabled);
        } catch (BeansException e) {
            return Optional.empty();
        }

    }

    public int getExtensionCount() {
        return context.getBeansOfType(Extension.class).size();
    }

}
