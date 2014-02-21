package nl.gmt.modules;

import nl.gmt.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModuleRegistration {
    private Module module;
    private String displayName;
    private List<Class<?>> dependencies;
    private Class<?> moduleType;
    private boolean loaded;

    ModuleRegistration(Class<?> moduleType, ModuleManager moduleManager) throws ModuleException {
        this.moduleType = moduleType;

        DisplayName displayNameAnnotation = moduleType.getAnnotation(DisplayName.class);
        AllowMultiple allowMultipleAnnotation = moduleType.getAnnotation(AllowMultiple.class);
        Dependencies dependenciesAnnotation = moduleType.getAnnotation(Dependencies.class);

        displayName = displayNameAnnotation.value();
        dependencies = Collections.unmodifiableList(Arrays.asList(
            dependenciesAnnotation != null && dependenciesAnnotation.value() != null
                ? dependenciesAnnotation.value()
                : new Class<?>[0]
        ));

        try {
            module = (Module)moduleType.newInstance();
        } catch (Throwable e) {
            throw new ModuleException("Cannot instantiate module", e);
        }

        module.setModuleManager(moduleManager);
    }

    public Module getModule() {
        return module;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    public Class<?> getModuleType() {
        return moduleType;
    }
}
