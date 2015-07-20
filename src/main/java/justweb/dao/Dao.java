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

    protected Dao(MongoService mongo) {
        this.mongo = mongo;
    }

    public abstract String collection();
    public abstract ModelType fromMongoDoc(Document doc);

    public ModelType[] fromMongoList(List<Document> docs) {
        ModelType[] array = (ModelType[]) new Object[docs.size()];
        for (int i = 0; i < docs.size(); i++)
            array[i] = fromMongoDoc(docs.get(i));
        return array;
    }

    @Suspendable
    public ModelType[] all() {
        return fromMongoList(mongo.find(collection(), null));
    }

    @Suspendable
    public void insert(ModelType model) {
        mongo.insert(collection(), model.toMongoDoc());
    }

    @Suspendable
    public void update(ModelType model, Bson fields) {
        mongo.updateOne(collection(), eq(Model.JP_ID, model.id), fields);
    }

}
