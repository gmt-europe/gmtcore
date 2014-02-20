package nl.gmt;

import java.util.HashMap;
import java.util.Map;

public class ServiceContainerImpl implements ServiceContainer, AutoCloseable {
    private Map<Class<?>, Object> services;
    private Class<?>[] defaultServices = {ServiceContainer.class, ServiceContainerImpl.class};
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

    public ServiceContainerImpl() {
    }

    public ServiceContainerImpl(ServiceProvider parentProvider) {
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

    public Object getService(Class<?> serviceType) {
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

        return obj;
    }

    @Override
    public void removeService(Class<?> serviceType) {
        removeService(serviceType, false);
    }

    @Override
    public void removeService(Class<?> serviceType, boolean promote) {
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
