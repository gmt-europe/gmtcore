package nl.gmt;

public interface ServiceProvider {
    Object getService(Class<?> serviceType);
}
