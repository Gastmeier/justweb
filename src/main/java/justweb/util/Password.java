package justweb.util;

import justweb.AppException;

import java.io.UnsupportedEncodingException;
import java.lang.AssertionError;import java.lang.String;import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Password {

    /**
     * A salt is used as an additional input to a one-way hash function for hashing a password. It defends against
     * dictionary and rainbow attacks.
     * @return The newly created salt.
     */
    public static byte[] createSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] createHash(int iterations, String password, byte[] salt) {
        if (iterations <= 0) throw new AppException("Iterations paramter needs to be greater or equal 1.");
        AppException.ifNull(password, "password");
        AppException.ifNullOrEmpty(salt, "salt");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(salt);

            byte[] passwordBytes = password.getBytes("UTF-8");
            passwordBytes = digest.digest(passwordBytes);

            for (int i = 0; i < iterations; i++) {
                digest.reset();
                passwordBytes = digest.digest(passwordBytes);
            }

            return passwordBytes;
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static boolean isSecure(String password) {
        return password != null && password.length() >= 6;
    }

}
