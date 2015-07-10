package justweb.services;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import justweb.util.Holder;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoService {

    private final MongoDatabase db;

    public MongoService(MongoDatabase db) {
        this.db = db;
    }

    @Suspendable
    public void createCollection(String name) {
        Holder<Boolean> contained = new Holder<>(false);

        Void v = async(callback -> {
            MongoIterable<Document> it = db.listCollections();
            it.forEach(document -> {
                if (document.get("name").equals(name))
                    contained.set(true);
            }, callback);
        });

        if (! contained.get())
            asyncVoid(callback -> db.createCollection(name, callback));
    }

    @Suspendable
    public Document findOne(String collectionName) {
        return async(callback -> {
            MongoCollection collection = db.getCollection(collectionName);
            FindIterable finder = collection.find();
            finder.first(callback);
        });
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
