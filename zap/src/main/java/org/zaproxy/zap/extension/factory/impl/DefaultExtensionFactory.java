package org.zaproxy.zap.extension.factory.impl;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.Extension;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.zaproxy.zap.extension.factory.ExtensionFactory;

/**
 * Extensions using
 * https://www.oracle.com/technical-resources/articles/javase/extensible.html
 *
 * @author username
 *
 */
@Configuration
public class DefaultExtensionFactory implements ExtensionFactory {

    private static Logger LOG = LogManager.getLogger(DefaultExtensionFactory.class);

    private final static String BUILT_IN_EXTENSIONS_PACKAGE = "org.zaproxy.zap.extension";

    private static final FileFilter EXTENSION_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getPath().toLowerCase().endsWith(".zap");
        }
    };

    private Set<Extension> buildInExtension;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Override
    public void loadAll() {
        loadExtensions(getBuildInExtension());
        loadFolders(new File(Constant.getZapHome(), Constant.FOLDER_PLUGIN),
                new File(Constant.getZapInstall(), Constant.FOLDER_PLUGIN));
    }

    private void loadExtensions(Set<Extension> extensions) {
        extensions.forEach(extension -> {
            // add example
            String beanName = getBeanName(extension);
            GenericBeanDefinition bd = new GenericBeanDefinition();
            bd.setBeanClass(extension.getClass());
            beanFactory.registerBeanDefinition(beanName, bd);
            System.out.println("registered bean: " + beanName);

//            Extension ext = context.getBean(extension.getClass());
//            System.out.println("found bean: " + getBeanName(ext) + " name:" + ext.getName());
//            unload(ext);

        });
    }

    @Override
    public void loadFolders(File... folders) {
        URLClassLoader ucl = getURLClassLoader(folders);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.setResourceLoader(new PathMatchingResourcePatternResolver(ucl));
        for (BeanDefinition bd : scanner.findCandidateComponents("org.zaproxy.zap.extension")) {
            System.out.println(bd.getBeanClassName());
        }
        // ServiceLoader<Extension> sl = ServiceLoader.load(Extension.class, ucl);
        // loadExtensions(new HashSet<>(IteratorUtils.toList(sl.iterator())));
    }

    private URLClassLoader getURLClassLoader(File... folders) {
        URL[] urls = Stream.of(folders)
                .filter(File::isDirectory)
                .map(f -> f.listFiles(EXTENSION_FILTER))
                .filter(ArrayUtils::isNotEmpty)
                .map(Arrays::asList)
                .flatMap(List::stream)
                .map(File::toURI)
                .map(t -> {
                    try {
                        return t.toURL();
                    } catch (MalformedURLException e) {
                        // NOP
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .toArray(new URL[] {});
        return new URLClassLoader(urls);
    }

    @Override
    public void unload(Extension extension) {
        beanFactory.destroyBean(extension);
        System.out.println("unregistered bean: " + getBeanName(extension));
    }

    private String getBeanName(Extension extension) {
        return "zapExt" + StringUtils.capitalize(StringUtils
                .defaultIfEmpty(StringUtils.substringAfterLast(extension.getName(), "."), extension.getName()));
    }

    private synchronized Set<Extension> getBuildInExtension() {
        if (buildInExtension == null) {
            buildInExtension = new Reflections(new ConfigurationBuilder().forPackages(BUILT_IN_EXTENSIONS_PACKAGE)
                    .setScanners(new SubTypesScanner())
                    .useParallelExecutor()).getSubTypesOf(Extension.class)
                            .stream()
                            .filter(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
                            .map(c -> {
                                try {
                                    return c.getConstructor().newInstance();
                                } catch (Exception e) {
                                    LOG.error(e.getMessage(), e);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
        }
        return buildInExtension;
    }

}
