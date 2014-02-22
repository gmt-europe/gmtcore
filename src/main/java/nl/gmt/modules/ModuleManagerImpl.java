package nl.gmt.modules;

import nl.gmt.ServiceProvider;
import org.apache.commons.lang.Validate;
import org.jboss.logging.Logger;

import java.lang.reflect.Array;
import java.util.*;

class ModuleManagerImpl implements ModuleManager {
    private static final Logger LOG = Logger.getLogger(ModuleManagerImpl.class);

    private ServiceProvider serviceProvider;
    private Map<Class<?>, ModuleRegistration> modules = new HashMap<>();
    private List<ModuleRegistration> loadOrder = new ArrayList<>();
    private boolean loaded;

    public ModuleManagerImpl(ServiceProvider serviceProvider, List<Class<?>> moduleTypes) throws ModuleException {
        this.serviceProvider = serviceProvider;

        loadModules(moduleTypes);
    }

    private void loadModules(List<Class<?>> moduleTypes) throws ModuleException {
        LOG.infof("Creating %d module registrations", moduleTypes.size());

        // Create the module registrations.

        for (Class<?> moduleType : moduleTypes) {
            modules.put(moduleType, new ModuleRegistration(moduleType, this));
        }

        verifyAllowMultiple();

        // Load the modules.

        LOG.info("Loading modules");

        determineLoadOrder();

        for (ModuleRegistration registration : loadOrder) {
            LOG.infof("Loading module %s (%s)", registration.getDisplayName(), registration.getModuleType());

            Module module = registration.getModule();

            boolean success = false;

            try {
                try {
                    module.loading();

                    module.load();

                    success = true;
                } finally {
                    module.loaded(success);
                }

                LOG.infof("Finished loading module %s (%s)", registration.getDisplayName(), registration.getModuleType());
            } catch (Exception e) {
                LOG.error(String.format("Exception when loading module %s", registration.getModuleType()), e);

                throw new ModuleException(String.format("Exception when loading module %s", registration.getModuleType()), e);
            }
        }

        loaded = true;
    }

    private void verifyAllowMultiple() throws ModuleException {
        LOG.info("Verifying @AllowMultiple annotations");

        for (ModuleRegistration registration : modules.values()) {
            // Find the class where the @AllowMultiple is defined; this is should be a base class.

            Class<?> declaring = registration.getModuleType();
            boolean allowMultiple = true;

            while (declaring != Module.class) {
                AllowMultiple allowMultipleAnnotation = declaring.getAnnotation(AllowMultiple.class);

                if (allowMultipleAnnotation != null) {
                    allowMultiple = allowMultipleAnnotation.value();
                    break;
                }

                declaring = declaring.getSuperclass();
            }

            if (allowMultiple) {
                continue;
            }

            // If we found an annotation and multiples are not allowed, check all registrations to see wheter
            // there is another one.

            for (ModuleRegistration otherRegistration : modules.values()) {
                if (
                    otherRegistration != registration &&
                    declaring.isAssignableFrom(otherRegistration.getModuleType())
                ) {
                    LOG.errorf("There cannot be multiple entries of the %s module", declaring);

                    throw new ModuleException(String.format("There cannot be multiple entries of the %s module", declaring));
                }
            }
        }
    }

    private void determineLoadOrder() throws ModuleException {
        LOG.info("Determining load order");

        Map<ModuleRegistration, Boolean> modules = new HashMap<>();

        for (ModuleRegistration registration : this.modules.values()) {
            modules.put(registration, false);
        }

        while (true) {
            // Find the set we want to order in this run.

            List<ModuleRegistration> toOrder = new ArrayList<ModuleRegistration>();

            for (Map.Entry<ModuleRegistration, Boolean> entry : modules.entrySet()) {
                if (!entry.getValue()) {
                    toOrder.add(entry.getKey());
                }
            }

            // Check whether we're done.

            if (toOrder.size() == 0) {
                break;
            }

            boolean hadOne = false;

            for (ModuleRegistration registration : toOrder) {
                // Check for dependencies.

                boolean haveUnloaded = false;

                for (Class<?> dependency : registration.getDependencies()) {
                    boolean haveDependency = false;
                    boolean dependencyExists = false;

                    // Check our modules list to see whether the dependency has been loaded.

                    for (Map.Entry<ModuleRegistration, Boolean> entry : modules.entrySet()) {
                        if (entry.getKey().getModuleType().isAssignableFrom(dependency)) {
                            dependencyExists = true;
                            haveDependency = entry.getValue();
                            break;
                        }
                    }

                    if (!dependencyExists) {
                        throw new ModuleException(String.format("Cannot find dependency %s", dependency));
                    }

                    if (!haveDependency) {
                        haveUnloaded = true;
                        break;
                    }
                }

                if (haveUnloaded) {
                    continue;
                }

                // All dependencies have been loaded; add this one to the load order.

                LOG.debugf("Added module %s to the load order", registration.getModuleType());

                hadOne = true;

                loadOrder.add(registration);

                modules.put(registration, true);
            }

            if (!hadOne) {
                for (Map.Entry<ModuleRegistration, Boolean> entry : modules.entrySet()) {
                    if (!entry.getValue()) {
                        LOG.errorf("Could not load module %s because load order could not be resolved", entry.getKey().getModuleType());
                    }
                }

                throw new ModuleException("Could not determine load order");
            }
        }

        LOG.info("Determined load order");
    }

    @Override
    public void close() throws Exception {
        if (!loaded) {
            return;
        }

        LOG.info("Unloading modules");

        unloadModules();

        loaded = false;
    }

    private void unloadModules() {
        for (int i = loadOrder.size() - 1; i >= 0; i--) {
            ModuleRegistration registration = loadOrder.get(i);

            LOG.infof("Unloading module %s (%s)", registration.getDisplayName(), registration.getModuleType());

            Module module = registration.getModule();

            try {
                boolean success = false;

                try {
                    module.unloading();

                    module.unload();

                    success = true;
                } finally {
                    module.unloaded(success);
                }

                LOG.infof("Finished unloading module %s (%s)", registration.getDisplayName(), registration.getModuleType());
            } catch (Exception e) {
                LOG.error(String.format("Exception when unloading module %s", registration.getModuleType()), e);
            }
        }
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        return serviceProvider.getService(serviceType);
    }

    @Override
    public <T extends Module> T findModule(Class<T> moduleType) {
        Validate.notNull(moduleType, "moduleType");

        T[] result = findModules(moduleType);

        switch (result.length) {
            case 0: return null;
            case 1: return result[0];
            default:
                LOG.warnf("Could not find module %s", moduleType);
                throw new IllegalArgumentException(String.format("More than one module implementing %s have been found", moduleType));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Module> T[] findModules(Class<T> moduleType) {
        Validate.notNull(moduleType, "moduleType");

        List<T> result = new ArrayList<>();

        for (Map.Entry<Class<?>, ModuleRegistration> entry : modules.entrySet()) {
            if (moduleType.isAssignableFrom(entry.getKey())) {
                result.add((T)entry.getValue().getModule());
            }
        }

        return result.toArray((T[])Array.newInstance(moduleType, result.size()));
    }
}
