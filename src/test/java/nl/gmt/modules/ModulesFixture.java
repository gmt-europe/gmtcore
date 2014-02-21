package nl.gmt.modules;

import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import com.sun.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import nl.gmt.DisplayName;
import nl.gmt.StandardServiceContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ModulesFixture {
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddModuleWithoutDisplayName() {
        new ModuleManagerBuilder()
            .addModule(ModuleWithoutDisplayName.class);
    }

    @Test(expected = ModuleException.class)
    public void cannotAddModuleWithoutDependency() throws ModuleException {
        new ModuleManagerBuilder()
            .addModule(ModuleB.class)
            .build(new StandardServiceContainer());
    }

    @Test
    public void canAddModuleWithDependency() throws ModuleException {
        new ModuleManagerBuilder()
            .addModule(ModuleA.class)
            .addModule(ModuleB.class)
            .build(new StandardServiceContainer());
    }

    @Test
    public void loadOrderWillBeCorrect() throws ModuleException {
        new ModuleManagerBuilder()
            .addModule(ModuleB.class)
            .addModule(ModuleA.class)
            .build(new StandardServiceContainer());
    }

    @Test(expected = ModuleException.class)
    public void cannotAddModulesWithCircularDependencies() throws ModuleException {
        new ModuleManagerBuilder()
            .addModule(CircularModuleA.class)
            .addModule(CircularModuleB.class)
            .build(new StandardServiceContainer());
    }

    @Test
    public void verifyLoadUnloadMethodsCalled() throws Exception {
        VerifyLoadModule module;

        try (ModuleManager manager = new ModuleManagerBuilder()
            .addModule(VerifyLoadModule.class)
            .build(new StandardServiceContainer())) {

            module = manager.findModule(VerifyLoadModule.class);

            assertNotNull(module);

            assertTrue(module.hadLoading);
            assertTrue(module.hadLoad);
            assertTrue(module.hadLoaded);
            assertTrue(module.loadedSuccess);
        }

        assertTrue(module.hadUnloading);
        assertTrue(module.hadUnload);
        assertTrue(module.hadUnloaded);
        assertTrue(module.unloadedSuccess);
    }

    @Test
    public void findOfBaseClassReturnsModule() throws Exception {
        try (ModuleManager manager = new ModuleManagerBuilder()
             .addModule(SingleModuleA.class)
             .build(new StandardServiceContainer())) {
            SingleModule module = manager.findModule(SingleModule.class);
            assertNotNull(module);
            assertTrue(module instanceof SingleModuleA);
        }
    }

    @Test
    public void throwingModuleStillHasUnloadedCalled() throws Exception {
        VerifyLoadModule module = null;

        try (ModuleManager manager = new ModuleManagerBuilder()
            .addModule(ThrowingModule.class)
            .build(new StandardServiceContainer())) {

            module = manager.findModule(VerifyLoadModule.class);

            assertNotNull(module);

            assertTrue(module.hadLoading);
            assertTrue(module.hadLoad);
            assertTrue(module.hadLoaded);
            assertTrue(module.loadedSuccess);
        }

        assertTrue(module.hadUnloading);
        assertTrue(module.hadUnload);
        assertTrue(module.hadUnloaded);
        assertFalse(module.unloadedSuccess);
    }

    @Test
    public void findModulesWillFindAll() throws ModuleException {
        ModuleManager manager = new ModuleManagerBuilder()
            .addModule(ModuleA.class)
            .addModule(ModuleB.class)
            .build(new StandardServiceContainer());

        List<Module> modules = Arrays.asList(manager.findModules(Module.class));

        assertEquals(2, modules.size());

        assertTrue(modules.indexOf(manager.findModule(ModuleA.class)) != -1);
        assertTrue(modules.indexOf(manager.findModule(ModuleB.class)) != -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwWhenMultipleModuleMatches() throws ModuleException {
        ModuleManager manager = new ModuleManagerBuilder()
            .addModule(ModuleA.class)
            .addModule(ModuleB.class)
            .build(new StandardServiceContainer());

        manager.findModule(Module.class);
    }

    @Test(expected = ModuleException.class)
    public void cannotLoadMultiple() throws ModuleException {
        new ModuleManagerBuilder()
            .addModule(SingleModuleA.class)
            .addModule(SingleModuleB.class)
            .build(new StandardServiceContainer());
    }

    @Test
    public void unloadOrderIsReverseOfLoad() throws Exception {
        final List<Module> loadOrder = new ArrayList<>();

        try {
            CallbackModule.loadCalled = new Function<CallbackModule>() {
                @Override
                public void call(CallbackModule value) {
                    loadOrder.add(value);
                }
            };

            CallbackModule.unloadCalled = new Function<CallbackModule>() {
                @Override
                public void call(CallbackModule value) {
                    loadOrder.add(value);
                }
            };

            ModuleA moduleA;
            ModuleB moduleB;

            try (ModuleManager manager = new ModuleManagerBuilder()
                 .addModule(ModuleA.class)
                 .addModule(ModuleB.class)
                 .build(new StandardServiceContainer())) {

                moduleA = manager.findModule(ModuleA.class);
                moduleB = manager.findModule(ModuleB.class);

                assertEquals(2, loadOrder.size());
                assertEquals(moduleA, loadOrder.get(0));
                assertEquals(moduleB, loadOrder.get(1));
            }

            assertEquals(4, loadOrder.size());
            assertEquals(moduleB, loadOrder.get(2));
            assertEquals(moduleA, loadOrder.get(3));
        } finally {
            CallbackModule.loadCalled = null;
            CallbackModule.unloadCalled = null;
        }
    }

    public static class CalledArgs {
        public boolean aLoadCalled;
        public boolean bLoadCalled;
        public boolean aUnloadCalled;
        public boolean bUnloadCalled;
    }

    public static abstract class CallbackModule extends Module {
        public static Function<CallbackModule> loadCalled;
        public static Function<CallbackModule> unloadCalled;

        @Override
        protected void load() throws Exception {
            if (loadCalled != null) {
                loadCalled.call(this);
            }
        }

        @Override
        protected void unload() throws Exception {
            if (unloadCalled != null) {
                unloadCalled.call(this);
            }
        }
    }

    @DisplayName("Module A")
    public static class ModuleA extends CallbackModule {
    }

    @DisplayName("Module B")
    @Dependencies({ModuleA.class})
    public static class ModuleB extends CallbackModule {
    }

    @DisplayName("Circular dependency module A")
    @Dependencies({CircularModuleB.class})
    public static class CircularModuleA extends Module {
        @Override
        protected void load() throws Exception {

        }

        @Override
        protected void unload() throws Exception {

        }
    }

    @DisplayName("Circular dependency module B")
    @Dependencies({CircularModuleA.class})
    public static class CircularModuleB extends Module {
        @Override
        protected void load() throws Exception {

        }

        @Override
        protected void unload() throws Exception {

        }
    }

    public static class ModuleWithoutDisplayName extends Module {
        @Override
        protected void load() throws Exception {

        }

        @Override
        protected void unload() throws Exception {

        }
    }

    @DisplayName("Verify load module")
    public static class VerifyLoadModule extends Module {
        public boolean hadLoading;
        public boolean hadLoad;
        public boolean hadLoaded;
        public boolean loadedSuccess;
        public boolean hadUnloading;
        public boolean hadUnload;
        public boolean hadUnloaded;
        public boolean unloadedSuccess;

        @Override
        protected void loading() throws Exception {
            hadLoading = true;
        }

        @Override
        protected void load() throws Exception {
            hadLoad = true;
        }

        @Override
        protected void loaded(boolean success) throws Exception {
            hadLoaded = true;
            loadedSuccess = success;
        }

        @Override
        protected void unloading() throws Exception {
            hadUnloading = true;
        }

        @Override
        protected void unload() throws Exception {
            hadUnload = true;
        }

        @Override
        protected void unloaded(boolean success) throws Exception {
            hadUnloaded = true;
            unloadedSuccess = success;
        }
    }

    public static class ThrowingModule extends VerifyLoadModule {
        @Override
        protected void unload() throws Exception {
            super.unload();
            throw new Exception();
        }
    }

    @AllowMultiple(false)
    public static abstract class SingleModule extends Module {
    }

    @DisplayName("Single module A")
    public static class SingleModuleA extends SingleModule {
        @Override
        protected void load() throws Exception {

        }

        @Override
        protected void unload() throws Exception {

        }
    }

    @DisplayName("Single module B")
    public static class SingleModuleB extends SingleModule {
        @Override
        protected void load() throws Exception {

        }

        @Override
        protected void unload() throws Exception {

        }
    }

    public static interface Function<T> {
        void call(T value);
    }
}
