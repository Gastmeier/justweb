package justweb.pico;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;
import justweb.Settings;
import org.picocontainer.injectors.Provider;

public class MongoDatabaseProvider implements Provider {

    public MongoDatabase provide(MongoClient client, Settings settings) {
        return client.getDatabase(settings.dbName());
    }

}
