package superperk.hug.testcontainers.containers;

import org.testcontainers.containers.GenericContainer;

import javax.annotation.PostConstruct;

public abstract class AbstractGenericContainer implements Container {
    protected String version;
    protected boolean reuse = true;

    protected abstract GenericContainer<?> getSourceContainer();

    @PostConstruct
    public abstract void init();

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }
}