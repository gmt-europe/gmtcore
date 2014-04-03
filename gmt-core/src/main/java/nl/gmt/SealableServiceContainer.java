package nl.gmt;

public class SealableServiceContainer extends StandardServiceContainer {
    private boolean sealed;

    @Override
    public void addService(Class<?> serviceType, Object serviceInstance, boolean promote) {
        verifyNotSealed();

        super.addService(serviceType, serviceInstance, promote);
    }

    @Override
    public void addService(Class<?> serviceType, ServiceCreatorCallback callback, boolean promote) {
        verifyNotSealed();

        super.addService(serviceType, callback, promote);
    }

    @Override
    public void removeService(Class<?> serviceType, boolean promote) {
        verifyNotSealed();

        super.removeService(serviceType, promote);
    }

    public void seal() {
        verifyNotSealed();

        sealed = true;
    }

    private void verifyNotSealed() {
        if (sealed) {
            throw new IllegalStateException("Service container cannot be modified");
        }
    }
}
