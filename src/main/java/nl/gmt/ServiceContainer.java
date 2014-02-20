package nl.gmt;

public interface ServiceContainer extends ServiceProvider, AutoCloseable {
    void addService(Class<?> serviceType, Object serviceInstance);

    void addService(Class<?> serviceType, Object serviceInstance, boolean promote);

    void addService(Class<?> serviceType, ServiceCreatorCallback callback);

    void addService(Class<?> serviceType, ServiceCreatorCallback callback, boolean promote);

    void removeService(Class<?> serviceType);

    void removeService(Class<?> serviceType, boolean promote);
}
