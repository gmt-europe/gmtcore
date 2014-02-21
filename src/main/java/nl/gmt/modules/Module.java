package nl.gmt.modules;

import nl.gmt.ServiceProvider;

public abstract class Module implements ServiceProvider {
    private ModuleManager moduleManager;

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    protected void loading() throws Exception {
    }

    protected abstract void load() throws Exception;

    protected void loaded(boolean success) throws Exception {
    }

    protected void unloading() throws Exception {
    }

    protected abstract void unload() throws Exception;

    protected void unloaded(boolean success) throws Exception {
    }

    public <T> T getService(Class<T> serviceType) {
        return moduleManager.getService(serviceType);
    }
}
