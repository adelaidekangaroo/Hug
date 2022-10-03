package superperk.hug.testcontainers.containers;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import superperk.hug.testcontainers.annotations.ContainerDependencies;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ContainerDependencies(initBefore = AbstractMongoClientConfiguration.class)
public final class MongoContainer extends AbstractGenericContainer implements Container {
    private MongoDBContainer sourceContainer;

    @Override
    public void init() {
        sourceContainer = new MongoDBContainer(DockerImageName.parse(version));
        sourceContainer.withReuse(reuse);
        sourceContainer.start();

        var port = URI.create(sourceContainer.getReplicaSetUrl()).getPort();

        System.setProperty("spring.data.mongodb.uri", sourceContainer.getReplicaSetUrl());
        System.setProperty("spring.data.mongodb.port", String.valueOf(port));
        System.setProperty("spring.data.mongodb.database", getDatabaseName());
    }

    @Override
    protected MongoDBContainer getSourceContainer() {
        return sourceContainer;
    }

    private String getDatabaseName() {
        var uri = sourceContainer.getReplicaSetUrl();
        var pattern = Pattern.compile("mongodb:(?://[^/]+/)?(\\w+)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("invalid database uri - " + uri);
    }
}