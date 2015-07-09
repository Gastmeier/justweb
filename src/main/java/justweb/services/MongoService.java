package justweb.services;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.bson.Document;

public class MongoService {

    private final MongoDatabase db;

    public MongoService(MongoDatabase db) {
        this.db = db;
    }

    @Suspendable
    public Document findOne(String collectionName) {
        Document document = null;
        try {
            document = new MongoAsync<Document>() {
                @Override
                protected void requestAsync() {
                    MongoCollection collection = db.getCollection(collectionName);
                    FindIterable finder = collection.find();
                    finder.first(this);
                }
            }.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return document;
    }

    private abstract class MongoAsync<TResult> extends FiberAsync<TResult, Throwable> implements SingleResultCallback<Document> {
        @Override
        public void onResult(Document result, Throwable t) {
            System.err.println(result.toString());
        }
    }

}
