package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class StringField extends JsonField<String, StringField> {

    public StringField(String name) { super(name, JsonNodeType.STRING); }

    public StringField max(int max) { custom(new MaxLengthValidator(max)); return this; }
    public StringField min(int min) { custom(new MinLengthValidator(min)); return this; }

    @Override
    public String value(JsonNode json) {
        json = json.get(getName());
        if (json == null)
            return null;
        return json.asText();
    }

    @Override
    public ValidationMisfit validateType(JsonNode json) { return null; }

}
