package nl.gmt;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

public class StandardServiceContainer implements ServiceContainer, AutoCloseable {
    private Map<Class<?>, Object> services;
    private Class<?>[] defaultServices = {ServiceContainer.class, StandardServiceContainer.class};
    private ServiceProvider parentProvider;

    private ServiceContainer getContainer() {
        if (parentProvider != null) {
            return (ServiceContainer)parentProvider.getService(ServiceContainer.class);
        }

        return null;
    }

    protected Class<?>[] getDefaultServices() {
        return defaultServices;
    }

    private Map<Class<?>, Object> getServices() {
        if (services == null) {
            services = new HashMap<>();
        }

        return services;
    }

    public StandardServiceContainer() {
    }

    public StandardServiceContainer(ServiceProvider parentProvider) {
        Validate.notNull(parentProvider, "parentProvider");

        this.parentProvider = parentProvider;
    }

    @Override
    public void addService(Class<?> serviceType, Object serviceInstance) {
        addService(serviceType, serviceInstance, false);
    }

    @Override
    public void addService(Class<?> serviceType, Object serviceInstance, boolean promote) {
        if (promote) {
            ServiceContainer container = getContainer();
            if (container != null) {
                container.addService(serviceType, serviceInstance, promote);
                return;
            }
        }

        Validate.notNull(serviceType, "serviceType");
        Validate.notNull(serviceInstance, "serviceInstance");

        if (!(serviceInstance instanceof ServiceCreatorCallback) && !serviceType.isInstance(serviceInstance)) {
            throw new IllegalArgumentException("Service instance is not of service type");
        }
        if (getServices().containsKey(serviceType)) {
            throw new IllegalArgumentException("Service already exists");
        }

        getServices().put(serviceType, serviceInstance);
    }

    @Override
    public void addService(Class<?> serviceType, ServiceCreatorCallback callback) {
        addService(serviceType, callback, false);
    }

    @Override
    public void addService(Class<?> serviceType, ServiceCreatorCallback callback, boolean promote) {
        if (promote) {
            ServiceContainer container = getContainer();
            if (container != null) {
                container.addService(serviceType, callback, promote);
                return;
            }
        }

        Validate.notNull(serviceType, "serviceType");
        Validate.notNull(callback, "callback");

        if (getServices().containsKey(serviceType)) {
            throw new IllegalArgumentException("Service already exists");
        }

        getServices().put(serviceType, callback);
    }

    @Override
    public void close() throws Exception {
        Map<Class<?>, Object> services = this.services;
        this.services = null;

        if (services == null) {
            return;
        }

        for (Object obj : services.values()) {
            if (obj instanceof AutoCloseable) {
                ((AutoCloseable)obj).close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getService(Class<T> serviceType) {
        Validate.notNull(serviceType, "serviceType");

        Object obj = null;
        for (Class<?> type : defaultServices) {
            if (type == serviceType) {
                obj = this;
                break;
            }
        }

        if (obj == null) {
            obj = getServices().get(serviceType);
        }

        if (obj instanceof ServiceCreatorCallback) {
            obj = ((ServiceCreatorCallback)obj).createService(this, serviceType);

            if (obj != null && !serviceType.isInstance(obj)) {
                obj = null;
            }

            getServices().put(serviceType, obj);
        }

        if (obj == null && parentProvider != null) {
            obj = parentProvider.getService(serviceType);
        }

        return (T)obj;
    }

    @Override
    public void removeService(Class<?> serviceType) {
        removeService(serviceType, false);
    }

    @Override
    public void removeService(Class<?> serviceType, boolean promote) {
        Validate.notNull(serviceType, "serviceType");

        if (promote) {
            ServiceContainer container = getContainer();
            if (container != null) {
                container.removeService(serviceType, promote);
                return;
            }
        }

        getServices().remove(serviceType);
    }
}
