package superperk.hug.extensions;

import com.mongodb.client.MongoClients;
import org.json.JSONException;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import superperk.hug.extensions.annotations.MongoInsert;
import superperk.hug.extensions.utils.MongoExtensionUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public final class MongoExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        MongoExtensionUtils.loadApplicationContext(context);

        var testClass = context.getRequiredTestClass();
        var dbUri = MongoExtensionUtils.getDatabaseURI();
        var dbName = MongoExtensionUtils.getDatabaseName();

        try (var client = MongoClients.create(dbUri)) {
            var database = client.getDatabase(dbName);
            Arrays.stream(testClass.getDeclaredAnnotationsByType(MongoInsert.class))
                    .forEach(insert -> {
                        var collection = database.getCollection(insert.collection());
                        collection.drop();
                        try {
                            var json = MongoExtensionUtils.readJsonFromFile(insert.location());
                            var documents = MongoExtensionUtils.parseJsonToDocuments(json);
                            collection.insertMany(documents);
                        } catch (URISyntaxException | IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        MongoExtensionUtils.dropDatabase();
    }
}