package justweb.pico;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import org.picocontainer.injectors.Provider;

public class MongoClientProvider implements Provider {

    public MongoClient provide() {
        return MongoClients.create();
    }

}
