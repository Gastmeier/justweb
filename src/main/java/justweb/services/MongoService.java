package justweb.services;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import justweb.AppException;
import justweb.util.Holder;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        return async(callback -> {
            MongoCollection<Document> collection = db.getCollection(collectionName);
            FindIterable<Document> finder = collection.find(filter);
            finder.first(callback);
        });
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
    private <TResult> TResult async(Consumer<MongoAsync<TResult>> requestAsync) {
        TResult result = null;

        try {
            result = new MongoAsync<TResult>() {
                @Override
                protected void requestAsync() {
                    requestAsync.accept(this);
                }
            }.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    @Suspendable
    private void asyncVoid(Consumer<MongoAsync<Void>> requestAsync) {
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

}
