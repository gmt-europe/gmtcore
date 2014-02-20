package nl.gmt;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServiceContainerFixture {
    @Test
    public void addAndRetrieveService() {
        ServiceContainer container = new ServiceContainerImpl();

        MyClass serviceInstance = new MyClass();

        container.addService(MyClass.class, serviceInstance);

        Assert.assertEquals(serviceInstance, container.getService(MyClass.class));
        Assert.assertNull(container.getService(MyInterface.class));
    }

    @Test
    public void addAndRetrieveByInterface() {
        ServiceContainer container = new ServiceContainerImpl();

        MyClass serviceInstance = new MyClass();

        container.addService(MyInterface.class, serviceInstance);

        Assert.assertEquals(serviceInstance, container.getService(MyInterface.class));
        Assert.assertNull(container.getService(MyClass.class));
    }

    @Test
    public void addAndRetrieveByCallback() {
        ServiceContainer container = new ServiceContainerImpl();

        final MyClass serviceInstance = new MyClass();

        container.addService(MyInterface.class, new ServiceCreatorCallback() {
            @Override
            public Object createService(ServiceContainer container, Class<?> serviceType) {
                return serviceInstance;
            }
        });

        Assert.assertEquals(serviceInstance, container.getService(MyInterface.class));
        Assert.assertNull(container.getService(MyClass.class));
    }

    @Test
    public void addPromotedAndRetrieveInBase() {
        ServiceContainer baseContainer = new ServiceContainerImpl();
        ServiceContainer container = new ServiceContainerImpl(baseContainer);

        MyClass serviceInstance = new MyClass();
        container.addService(MyClass.class, serviceInstance, true);

        Assert.assertEquals(serviceInstance, container.getService(MyClass.class));
        Assert.assertEquals(serviceInstance, baseContainer.getService(MyClass.class));
    }

    @Test
    public void closeClosesInstances() throws Exception {
        MyClass serviceInstance = new MyClass();

        try (ServiceContainer container = new ServiceContainerImpl()) {
            container.addService(MyClass.class, serviceInstance);
        }

        Assert.assertTrue(serviceInstance.isClosed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addForInvalidTypeThrows() {
        ServiceContainer container = new ServiceContainerImpl();

        container.addService(Integer.class, new MyClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void doubleAddThrows() {
        ServiceContainer container = new ServiceContainerImpl();

        container.addService(MyClass.class, new MyClass());
        container.addService(MyClass.class, new MyClass());
    }

    @Test
    public void returnsSelfAsDefaultService() {
        ServiceContainer container = new ServiceContainerImpl();

        Assert.assertEquals(container, container.getService(ServiceContainer.class));
        Assert.assertEquals(container, container.getService(ServiceContainerImpl.class));
    }

    @Test
    public void canRemoveService() {
        ServiceContainer container = new ServiceContainerImpl();

        MyClass serviceInstance = new MyClass();

        container.addService(MyClass.class, serviceInstance);

        Assert.assertEquals(serviceInstance, container.getService(MyClass.class));

        container.removeService(MyClass.class);

        Assert.assertNull(container.getService(MyClass.class));
    }

    @Test
    public void promoteRemoveWillRemoveFromBase() {
        ServiceContainer baseContainer = new ServiceContainerImpl();
        ServiceContainer container = new ServiceContainerImpl(baseContainer);

        MyClass serviceInstance = new MyClass();
        container.addService(MyClass.class, serviceInstance, true);

        Assert.assertEquals(serviceInstance, container.getService(MyClass.class));
        Assert.assertEquals(serviceInstance, baseContainer.getService(MyClass.class));

        container.removeService(MyClass.class, true);

        Assert.assertNull(container.getService(MyClass.class));
        Assert.assertNull(baseContainer.getService(MyClass.class));
    }

    private static interface MyInterface extends AutoCloseable {
    }

    private static class MyClass implements MyInterface {
        private boolean closed;

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws Exception {
            closed = true;
        }
    }
}
