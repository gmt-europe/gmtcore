package nl.gmt;

public interface ServiceProvider {
    <T> T getService(Class<T> serviceType);
}
