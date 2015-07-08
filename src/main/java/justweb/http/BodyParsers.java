package justweb.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;

public class BodyParsers {

    protected final ObjectMapper jsonMapper;

    public BodyParsers(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public JsonNode json(byte[] body) {
        String json = new String(body, Charset.forName("UTF-8"));
        try {
            return jsonMapper.reader().readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
