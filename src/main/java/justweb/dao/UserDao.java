package justweb.dao;

import co.paralleluniverse.fibers.Suspendable;
import justweb.models.User;
import justweb.services.MongoService;

import static com.mongodb.client.model.Filters.eq;

public abstract class UserDao<UserType extends User> extends Dao<UserType> {

    protected UserDao(MongoService mongo) {
        super(mongo);
    }

    @Override
    public String collection() {
        return "users";
    }

    public void init() {
        mongo.createCollection(collection());
    }

    @Suspendable
    public UserType oneByEmail(String email) {
        return fromMongoDoc(mongo.findOne(collection(), eq(User.JP_EMAIL, email)));
    }

    @Suspendable
    public UserType oneByActivation(String activation) {
        return fromMongoDoc(mongo.findOne(collection(), eq(User.JP_ACTIVATION, activation)));
    }

}
