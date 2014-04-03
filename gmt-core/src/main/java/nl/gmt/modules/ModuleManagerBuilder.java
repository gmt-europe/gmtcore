package nl.gmt.modules;

import nl.gmt.DisplayName;
import nl.gmt.ServiceContainer;
import nl.gmt.ServiceProvider;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;

public class ModuleManagerBuilder {
    private final List<Class<?>> moduleTypes = new ArrayList<>();

    public ModuleManagerBuilder addModule(Class<?> moduleType) {
        Validate.notNull(moduleType, "moduleType");

        if (!Module.class.isAssignableFrom(moduleType)) {
            throw new IllegalArgumentException("Module type must be a Module");
        }
        if (moduleType.getAnnotation(DisplayName.class) == null) {
            throw new IllegalArgumentException("Module type must have a DisplayName annotation");
        }

        moduleTypes.add(moduleType);
        return this;
    }

    public ModuleManager build(ServiceProvider serviceProvider) throws ModuleException {
        return build(serviceProvider, false);
    }

    public ModuleManager build(ServiceProvider serviceProvider, boolean addToContainer) throws ModuleException {
        Validate.notNull(serviceProvider, "serviceProvider");

        ModuleManagerImpl moduleManager = new ModuleManagerImpl(serviceProvider, moduleTypes);

        if (addToContainer) {
            serviceProvider.getService(ServiceContainer.class).addService(ModuleManager.class, moduleManager);
        }

        moduleManager.loadModules();

        return moduleManager;
    }
}
