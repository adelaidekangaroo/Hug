package superperk.hug.testcontainers.definitions;

public final class PostgresContainerDefinition extends AbstractContainerDefinition {
    public PostgresContainerDefinition(String image) {
        super(ContainerType.POSTGRES, image);
    }
}