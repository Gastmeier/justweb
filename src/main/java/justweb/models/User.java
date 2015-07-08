package justweb.models;

import justweb.AppException;
import justweb.util.Password;

import java.util.Base64;

public class User {

    private String email;
    private String password;
    private String salt;

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

}
