package justweb.models;

import justweb.AppException;
import justweb.util.Password;
import org.bson.Document;

import java.util.Base64;

public class User extends Model {

    public static final String JP_EMAIL = "email";
    public static final String JP_PASSWORD = "password";
    public static final String JP_ACTIVATION = "activation";

    private String email;
    private String password;
    private String activation;
    private String salt; // TODO: obsolete?

    public void setPassword(String password) {
        AppException.ifNull(password, "password");
        final byte[] salt = Password.createSalt();
        final byte[] hashed = Password.createHash(1000, password, salt);

        this.salt = Base64.getEncoder().encodeToString(salt);
        this.password = Base64.getEncoder().encodeToString(hashed);
    }

    public boolean passwordEquals(String givenPassword) {
        AppException.ifNull(givenPassword, "givenPassword", "If there is no password to compare, do not call this method.");
        AppException.ifNull(password, "password", "Set a password first.");
        AppException.ifNull(salt, "salt", "Set a password first.");

        byte[] saltBytes = Base64.getDecoder().decode(salt);
        final byte[] hashBytes = Password.createHash(1000, givenPassword, saltBytes);
        final String b64Password = Base64.getEncoder().encodeToString(hashBytes);
        return password.equals(b64Password);
    }

    @Override
    public Document toMongoDoc() {
        Document doc = super.toMongoDoc();
        doc.append(User.JP_EMAIL, email);
        doc.append(User.JP_PASSWORD, password);
        doc.append(User.JP_ACTIVATION, activation);
        return doc;
    }
}
