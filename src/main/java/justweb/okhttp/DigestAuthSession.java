package justweb.okhttp;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class DigestAuthSession {

    private static final char[] HEXADECIMAL = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final String SEPARATORS = " ;,:@()<>\\\"/[]?={}\t";
    private static final String UNSAFE_CHARS = "\"\\";


    private static final int QOP_UNKNOWN = -1;
    private static final int QOP_MISSING = 0;
    private static final int QOP_AUTH_INT = 1;
    private static final int QOP_AUTH = 2;

    private final Request initialRequest;
    private final String user;
    private final String pwd;
    private final String credentialsCharset;
    private String lastNonce;
    private long nounceCount;
    private String cnonce;
    private String a1;
    private String a2;

    public DigestAuthSession(Request initialRequest, String user, String pwd, String credentialsCharset) {
        this.initialRequest = initialRequest;
        this.user = user;
        this.pwd = pwd;
        this.credentialsCharset = credentialsCharset != null ? credentialsCharset : "ISO-8859-1";
    }

    public String createHeader(Response response)
            throws AuthenticationException {
        String authHeader = response.header("www-authenticate");

        if (authHeader == null)
            throw new AuthenticationException("The given response does not contain the 'www-authenticate' header.");
        if (! authHeader.toLowerCase(Locale.ROOT).startsWith("digest"))
            throw new AuthenticationException("The given 'www-authenticate' header does not refer to the digest method.");

        HashMap<String, String> authParams = new HashMap<>(11, 1);

        String[] splitParams = authHeader.substring("digest".length()).split(",");
        for (String param : splitParams) {
            String[] splitParam = param.split("=");

            if (splitParam.length != 2)
                throw new AuthenticationException(String.format(
                        "Could not parse parameter value pair '%s' in 'www-authenticate' header.", param));

            final String name = splitParam[0].trim().toLowerCase();
            String value = splitParam[1].trim();
            if (value.startsWith("\"") && value.endsWith("\""))
                value = value.substring(1, value.length() - 1);

            authParams.put(name, value);
        }

        if (! authParams.containsKey("realm"))
            throw new AuthenticationException("Missing 'realm' in 'www-authenticate' header for digest authentication.");
        if (! authParams.containsKey("nonce"))
            throw new AuthenticationException("Missing 'nonce' in 'www-authenticate' header for digest authentication.");

        final String uri = uri();
        final String method = initialRequest.method();
        final String charset = response.header("charset", credentialsCharset);
        final String realm = authParams.get("realm");
        final String nonce = authParams.get("nonce");
        final String opaque = authParams.getOrDefault("opaque", null);
        final String algorithm = authParams.getOrDefault("algorithm", "MD5");
        final String qopString = authParams.getOrDefault("qop", null);

        int qop = QOP_UNKNOWN;

        Set<String> qopSet = new HashSet<>(4);
        if (qopString != null) {
            final StringTokenizer tok = new StringTokenizer(qopString, ",");

            while (tok.hasMoreTokens()) {
                final String variant = tok.nextToken().trim();
                qopSet.add(variant.toLowerCase(Locale.ROOT));
            }

            if (qopSet.contains("auth"))
                qop = QOP_AUTH;
        }
        else {
            qop = QOP_MISSING;
        }

        if (qop == QOP_UNKNOWN)
            throw new AuthenticationException("None of the qop methods is supported: " + qopString);

        String digAlg = algorithm;
        if (digAlg.equalsIgnoreCase("MD5-sess")) {
            digAlg = "MD5";
        }

        final MessageDigest digester;
        try {
            digester = MessageDigest.getInstance(digAlg);
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException("No algorithm found for: " + digAlg);
        }

        if (nonce.equals(lastNonce)) {
            nounceCount++;
        } else {
            nounceCount = 1;
            cnonce = null;
            lastNonce = nonce;
        }
        final StringBuilder sb = new StringBuilder(256);
        final Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%08x", Long.valueOf(nounceCount));
        formatter.close();
        final String nc = sb.toString();

        if (cnonce == null) {
            cnonce = createCnonce();
        }

        a1 = null;
        a2 = null;

        if (algorithm.equalsIgnoreCase("MD5-sess")) {
            // calculated one per session
            sb.setLength(0);
            sb.append(user).append(':').append(realm).append(':').append(pwd);
            final String checksum = encode(digester.digest(bytes(sb.toString(), charset)));
            sb.setLength(0);
            sb.append(checksum).append(':').append(nonce).append(':').append(cnonce);
            a1 = sb.toString();
        } else {
            sb.setLength(0);
            sb.append(user).append(':').append(realm).append(':').append(pwd);
            a1 = sb.toString();
        }

        final String hasha1 = encode(digester.digest(bytes(a1, charset)));

        if (qop == QOP_AUTH) {
            // Method ":" digest-uri-value
            a2 = method + ':' + uri;
        } else {
            a2 = method + ':' + uri;
        }

        final String hasha2 = encode(digester.digest(bytes(a2, charset)));

        final String digestValue;
        if (qop == QOP_MISSING) {
            sb.setLength(0);
            sb.append(hasha1).append(':').append(nonce).append(':').append(hasha2);
            digestValue = sb.toString();
        } else {
            sb.setLength(0);
            sb.append(hasha1).append(':').append(nonce).append(':').append(nc).append(':')
                    .append(cnonce).append(':').append(qop == QOP_AUTH_INT ? "auth-int" : "auth")
                    .append(':').append(hasha2);
            digestValue = sb.toString();
        }

        final String digest = encode(digester.digest(bytes(digestValue, "US-ASCII")));

        sb.setLength(0);
//        if (false/*isProxy()*/) {
//            sb.append("Proxy-Authorization: ");
//        } else {
//            sb.append("Authorization: ");
//        }
        sb.append("Digest ");

        final Map<String, String> params = new LinkedHashMap<>(10, 1);

        params.put("username", user);
        params.put("realm", realm);
        params.put("nonce", nonce);
        params.put("uri", uri);
        params.put("response", digest);

        if (qop != QOP_MISSING) {
            params.put("qop", qop == QOP_AUTH_INT ? "auth-int" : "auth");
            params.put("nc", nc);
            params.put("cnonce", cnonce);
        }
        // algorithm cannot be null here
        params.put("algorithm", algorithm);
        if (opaque != null) {
            params.put("opaque", opaque);
        }

        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (! first)
                sb.append(", ");
            else
                first = false;

            final String name = entry.getKey();
            final String value = entry.getValue();

            sb.append(name);

            final boolean quotesIfNeeded = "nc".equals(name) || "qop".equals(name) || "algorithm".equals(name);

            if (value != null) {
                sb.append("=");

                boolean quotes = ! quotesIfNeeded;
                if (quotesIfNeeded) {
                    for (int i = 0; (i < value.length()) && ! quotes; i++) {
                        quotes = SEPARATORS.indexOf(value.charAt(i)) >= 0;
                    }
                }

                if (quotes) {
                    sb.append('"');
                }

                for (int i = 0; i < value.length(); i++) {
                    final char ch = value.charAt(i);

                    if (UNSAFE_CHARS.indexOf(ch) >= 0) {
                        sb.append('\\');
                    }

                    sb.append(ch);
                }

                if (quotes) {
                    sb.append('"');
                }
            }
        }
        return sb.toString();
    }

    private String createCnonce() {
        final SecureRandom rnd = new SecureRandom();
        final byte[] tmp = new byte[8];
        rnd.nextBytes(tmp);
        return encode(tmp);
    }

    private String encode(final byte[] binaryData) {
        final int n = binaryData.length;
        final char[] buffer = new char[n * 2];
        for (int i = 0; i < n; i++) {
            final int low = (binaryData[i] & 0x0f);
            final int high = ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[(i * 2) + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);
    }

    private byte[] bytes(String data, String charset) throws AuthenticationException {
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new AuthenticationException("Given encoding '" + charset + "' is not supported.");
        }
    }

    private String uri() throws AuthenticationException {
        try {
            return initialRequest.uri().getPath();
        } catch (IOException e) {
            throw new AuthenticationException("Could not read uri from initial request.");
        }
    }

}
