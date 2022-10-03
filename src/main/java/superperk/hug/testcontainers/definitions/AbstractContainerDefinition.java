package superperk.hug.testcontainers.definitions;

import lombok.Getter;
import superperk.hug.testcontainers.containers.MongoContainer;
import superperk.hug.testcontainers.containers.PostgresContainer;

import java.beans.Introspector;

@Getter
public abstract class AbstractContainerDefinition {

    protected ContainerType containerType;
    protected String image;

    public AbstractContainerDefinition(ContainerType containerType, String image) {
        this.containerType = containerType;
        this.image = image;
    }

    @Getter
    public enum ContainerType {
        POSTGRES(PostgresContainer.class),
        MONGO(MongoContainer.class);
       /*  REDIS(PostgresContainer.class),
        RABBIT(PostgresContainer.class),
        KAFKA(PostgresContainer.class);*/

        private final Class<?> beanType;
        private final String beanName;

        ContainerType(Class<?> beanType) {
            this.beanType = beanType;
            this.beanName = Introspector.decapitalize(beanType.getSimpleName());
        }
    }
}