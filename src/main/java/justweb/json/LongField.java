package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class LongField extends JsonField<Long, LongField> {

    public LongField(String name) { super(name, JsonNodeType.NUMBER); }

    @Override
    public Long value(JsonNode json) {
        try {
            json = json.get(getName());
            if (json == null)
                return null;
            return Long.parseLong(json.asText());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public ValidationMisfit validateType(JsonNode json) {
        try {
            Long.parseLong(json.asText());
            return null;
        }
        catch (NumberFormatException e) {
            return new NotLongMisfit(this);
        }
    }

    public static class NotLongMisfit extends ValidationMisfit<LongField> {
        public NotLongMisfit(LongField validator) {
            super(validator, "");
        }
    }

}
