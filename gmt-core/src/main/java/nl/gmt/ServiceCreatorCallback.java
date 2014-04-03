package nl.gmt;

public interface ServiceCreatorCallback {
    Object createService(ServiceContainer container, Class<?> serviceType);
}
