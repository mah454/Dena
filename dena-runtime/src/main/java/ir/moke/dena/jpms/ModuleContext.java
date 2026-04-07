package ir.moke.dena.jpms;

import ir.moke.dena.api.IModule;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ModuleContext {
    private String name;
    private String version;
    private boolean running = false;
    private ModuleLayer layer;
    private ExecutorService executorService;
    private IModule iModule;
    private URLClassLoader classLoader;
    private Path path;
    private String description;
    private String maintainer;
    private String url;

    public ModuleContext() {
    }

    public ModuleContext(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public ModuleContext(ModuleLayer layer, Path path, String name, String description, String maintainer, String url) {
        this.layer = layer;
        this.path = path;
        this.name = name;
        this.description = description;
        this.maintainer = maintainer;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ModuleLayer getLayer() {
        return layer;
    }

    public void setLayer(ModuleLayer layer) {
        this.layer = layer;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public IModule getIModule() {
        return iModule;
    }

    public void setIModule(IModule iModule) {
        this.iModule = iModule;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleContext moduleContext = (ModuleContext) o;
        return Objects.equals(name, moduleContext.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Name: %s | Started: %s | Maintainer: %s | Path: %s".formatted(name, isRunning(), maintainer, path);
    }

    public String toJson() {
        return """
                {"name": "%s","version": "%s","running": "%s","path": "%s","description": "%s","maintainer": "%s","url": "%s"}
                """
                .formatted(name, version, running, path.toString(), description, maintainer, url)
                .replaceAll("\"null\"","null")
                .trim();
    }
}
