package nl.gmt.modules;

import nl.gmt.ServiceProvider;

public interface ModuleManager extends ServiceProvider, AutoCloseable {
    <T extends Module> T findModule(Class<T> moduleType);
    <T extends Module> T[] findModules(Class<T> moduleType);
}
