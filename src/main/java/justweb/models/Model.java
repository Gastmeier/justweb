package justweb.models;

import org.bson.Document;
import org.bson.types.ObjectId;import java.lang.String;

public abstract class Model {

    public static final String JP_ID = "_id";

    public final ObjectId id;

    public Model() {
        this.id = ObjectId.get();
    }

    public Model(ObjectId id) {
        this.id = id;
    }

    public Document toMongoDoc() {
        Document doc = new Document();
        doc.append(JP_ID, id);
        return doc;
    }
}
