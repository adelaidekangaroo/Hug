package superperk.hug.testcontainers.containers;

public interface Container {
    void setVersion(String version);

    void setReuse(boolean reuse);
}