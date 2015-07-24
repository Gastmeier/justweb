package justweb.dao;

import co.paralleluniverse.fibers.Suspendable;
import justweb.models.Model;
import justweb.services.MongoService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public abstract class Dao<ModelType extends Model> {

    protected final MongoService mongo;

    public Dao(MongoService mongo) {
        this.mongo = mongo;
    }

    public abstract String collection();
    public abstract ModelType fromMongoDoc(Document doc);

    @Suspendable
    public void createCollection() {
        mongo.createCollection(collection());
    }

    @Suspendable
    public List<ModelType> all() {
        return fromMongoList(mongo.find(collection(), null));
    }

    @Suspendable
    public void insert(ModelType model) {
        mongo.insert(collection(), model.toMongoDoc());
    }

    @Suspendable
    public void update(ModelType model, Bson update) {
        mongo.updateOne(collection(), eq(Model.JP_ID, model.id), update);
    }

    public List<ModelType> fromMongoList(List<Document> docs) {
        List<ModelType> converted = new ArrayList<>(docs.size());
        for (Document doc : docs)
            converted.add(fromMongoDoc(doc));
        return converted;
    }
}
