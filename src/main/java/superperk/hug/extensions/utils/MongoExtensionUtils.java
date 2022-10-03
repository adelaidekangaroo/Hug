package superperk.hug.extensions.utils;

import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class MongoExtensionUtils {

    private MongoExtensionUtils() {
    }

    public static void dropDatabase() {
        var dbUri = MongoExtensionUtils.getDatabaseURI();
        var dbName = MongoExtensionUtils.getDatabaseName();
        try (var client = MongoClients.create(dbUri)) {
            var database = client.getDatabase(dbName);
            database.drop();
        } catch (Exception e) {
            throw new RuntimeException(dbName + " database drop failed " + e);
        }
    }

    public static List<Document> parseJsonToDocuments(String json) throws JSONException {
        var jsonArray = new JSONArray(json);
        var documents = new LinkedList<Document>();
        for (int i = 0; i < jsonArray.length(); i++) {
            var jsonObject = jsonArray.getJSONObject(i);
            var document = Document.parse(jsonObject.toString());
            documents.add(document);
        }
        return documents;
    }

    public static String readJsonFromFile(String fileName) throws IOException, URISyntaxException {
        var uri = ClassLoader.getSystemResource(fileName).toURI();
        var path = Path.of(uri);
        var bytesRead = Files.readAllBytes(path);
        return new String(bytesRead);
    }

    public static String getDatabaseName() {
        return System.getProperty("spring.data.mongodb.database");
    }

    public static String getDatabaseURI() {
        return System.getProperty("spring.data.mongodb.uri");
    }

    public static void loadApplicationContext(ExtensionContext context) {
        SpringExtension.getApplicationContext(context); // load context
    }
}