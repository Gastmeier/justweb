package justweb.services;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.client.result.UpdateResult;
import justweb.AppException;
import justweb.util.Holder;
import justweb.util.SuspendableConsumer;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoService {

    private final MongoDatabase db;

    public MongoService(MongoDatabase db) {
        this.db = db;
    }

    @Suspendable
    public boolean collectionExists(String name) {
        Holder<Boolean> contained = new Holder<>(false);

        asyncVoid(callback -> {
            MongoIterable<Document> it = db.listCollections();
            it.forEach(document -> {
                if (document.get("name").equals(name))
                    contained.set(true);
            }, callback);
        });

        return contained.get();
    }

    @Suspendable
    public void createCollection(String name) {
        if (! collectionExists(name))
            asyncVoid(callback -> db.createCollection(name, callback));
    }

    @Suspendable
    public List<Document> find(String collectionName, Bson filter) {
        Holder<List<Document>> found = new Holder<>(new ArrayList<>());

        asyncVoid(callback -> {
            MongoCollection<Document> collection = db.getCollection(collectionName);
            FindIterable<Document> finder = collection.find(filter);
            finder.forEach(document -> found.get().add(document), callback);
        });

        return found.get();
    }

    @Suspendable
    public Document findFirst(String collectionName, Bson filter) {
        return async(callback -> coll(collectionName).find(filter).first(callback));
    }

    @Suspendable
    public Document findOne(String collectionName, Bson filter) {
        List<Document> found = find(collectionName, filter);

        if (found.size() > 1) {
            StringBuilder message = new StringBuilder("There was more than one result. The documents with the following ids where found: ");
            boolean first = true;

            for (Document doc : found) {
                if (first)
                    first = false;
                else
                    message.append(", ");
                message.append(doc.get("_id"));
            }

            throw new AppException(message.toString());
        }

        return found.isEmpty() ? null : found.get(0);
    }

    @Suspendable
    public void insert(String collectionName, Document document) {
        asyncVoid(callback -> coll(collectionName).insertOne(document, callback));
    }

    @Suspendable
    public void updateOne(String collectionName, Bson filter, Bson fields) {
        Bson update = new Document("$set", fields);
        UpdateResult result = async(callback -> coll(collectionName).updateOne(filter, update, callback));
        // TODO: do something with the result
    }

    @Suspendable
    private <TResult> TResult async(SuspendableConsumer<MongoAsync<TResult>> requestAsync) {
        TResult result = null;

        try {
            result = new MongoAsync<TResult>() {
                @Override
                protected void requestAsync() {
                    requestAsync.accept(this);
                }
            }.run();
        } catch (InterruptedException e) {
            // TODO: was interrupted, what now?
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    @Suspendable
    private void asyncVoid(SuspendableConsumer<MongoAsync<Void>> requestAsync) {
        async(requestAsync);
    }

    private abstract class MongoAsync<TResult> extends FiberAsync<TResult, Throwable> implements SingleResultCallback<TResult> {
        @Override
        public void onResult(TResult result, Throwable t) {
            if (result != null && t == null) {
                asyncCompleted(result);
            }
            else if (t != null) {
                asyncFailed(t);
            }
            else {
                asyncCompleted(null);
            }
        }
    }

    protected MongoCollection<Document> coll(String collectionName) {
        return db.getCollection(collectionName);
    }

}
