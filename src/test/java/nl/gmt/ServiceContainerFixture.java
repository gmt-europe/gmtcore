package nl.gmt;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ServiceContainerFixture {
    @Test
    public void addAndRetrieveService() {
        ServiceContainer container = new StandardServiceContainer();

        MyClass serviceInstance = new MyClass();

        container.addService(MyClass.class, serviceInstance);

        assertEquals(serviceInstance, container.getService(MyClass.class));
        assertNull(container.getService(MyInterface.class));
    }

    @Test
    public void addAndRetrieveByInterface() {
        ServiceContainer container = new StandardServiceContainer();

        MyClass serviceInstance = new MyClass();

        container.addService(MyInterface.class, serviceInstance);

        assertEquals(serviceInstance, container.getService(MyInterface.class));
        assertNull(container.getService(MyClass.class));
    }

    @Test
    public void addAndRetrieveByCallback() {
        ServiceContainer container = new StandardServiceContainer();

        final MyClass serviceInstance = new MyClass();

        container.addService(MyInterface.class, new ServiceCreatorCallback() {
            @Override
            public Object createService(ServiceContainer container, Class<?> serviceType) {
                return serviceInstance;
            }
        });

        assertEquals(serviceInstance, container.getService(MyInterface.class));
        assertNull(container.getService(MyClass.class));
    }

    @Test
    public void addPromotedAndRetrieveInBase() {
        ServiceContainer baseContainer = new StandardServiceContainer();
        ServiceContainer container = new StandardServiceContainer(baseContainer);

        MyClass serviceInstance = new MyClass();
        container.addService(MyClass.class, serviceInstance, true);

        assertEquals(serviceInstance, container.getService(MyClass.class));
        assertEquals(serviceInstance, baseContainer.getService(MyClass.class));
    }

    @Test
    public void closeClosesInstances() throws Exception {
        MyClass serviceInstance = new MyClass();

        try (ServiceContainer container = new StandardServiceContainer()) {
            container.addService(MyClass.class, serviceInstance);
        }

        assertTrue(serviceInstance.isClosed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addForInvalidTypeThrows() {
        ServiceContainer container = new StandardServiceContainer();

        container.addService(Integer.class, new MyClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void doubleAddThrows() {
        ServiceContainer container = new StandardServiceContainer();

        container.addService(MyClass.class, new MyClass());
        container.addService(MyClass.class, new MyClass());
    }

    @Test
    public void returnsSelfAsDefaultService() {
        ServiceContainer container = new StandardServiceContainer();

        assertEquals(container, container.getService(ServiceContainer.class));
        assertEquals(container, container.getService(StandardServiceContainer.class));
    }

    @Test
    public void canRemoveService() {
        ServiceContainer container = new StandardServiceContainer();

        MyClass serviceInstance = new MyClass();

        container.addService(MyClass.class, serviceInstance);

        assertEquals(serviceInstance, container.getService(MyClass.class));

        container.removeService(MyClass.class);

        assertNull(container.getService(MyClass.class));
    }

    @Test
    public void promoteRemoveWillRemoveFromBase() {
        ServiceContainer baseContainer = new StandardServiceContainer();
        ServiceContainer container = new StandardServiceContainer(baseContainer);

        MyClass serviceInstance = new MyClass();
        container.addService(MyClass.class, serviceInstance, true);

        assertEquals(serviceInstance, container.getService(MyClass.class));
        assertEquals(serviceInstance, baseContainer.getService(MyClass.class));

        container.removeService(MyClass.class, true);

        assertNull(container.getService(MyClass.class));
        assertNull(baseContainer.getService(MyClass.class));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotAddAfterSeal() {
        SealableServiceContainer container = new SealableServiceContainer();

        container.seal();

        container.addService(MyClass.class, new MyClass());
    }

    @Test(expected = IllegalStateException.class)
    public void cannotRemoveAfterSeal() {
        SealableServiceContainer container = new SealableServiceContainer();

        container.addService(MyClass.class, new MyClass());

        container.seal();

        container.removeService(MyClass.class);
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
