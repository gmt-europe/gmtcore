package nl.gmt.modules;

import org.apache.commons.lang.Validate;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.net.URL;

public class ModuleManagerBuilderUtils {
    private ModuleManagerBuilderUtils() {
    }

    public static ModuleManagerBuilder addModulesFromUrls(ModuleManagerBuilder builder, URL... urls) {
        Validate.notNull(urls, "urls");

        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setScanners(new SubTypesScanner())
            .setUrls(urls)
        );

        for (Class<? extends Module> moduleClass : reflections.getSubTypesOf(Module.class)) {
            if (!Modifier.isAbstract(moduleClass.getModifiers())) {
                builder.addModule(moduleClass);
            }
        }

        return builder;
    }
}
