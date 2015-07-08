package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import justweb.Misfit;

import java.util.ArrayList;
import java.util.Optional;

public abstract class JsonField<ValueType, ThisType extends JsonField> extends Validator {

    private final String name;
    private final JsonNodeType type;
    private ArrayList<Validator> validators = new ArrayList<>();

    public JsonField(String name, JsonNodeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }

    @Override
    public Misfit validate(Optional<JsonNode> maybeJson) {
        JsonNode json = maybeJson.orElse(null);
        if (json != null && ! json.isNull()) {
            Misfit misfit = validateType(json);

            if (misfit != null)
                return misfit;
        }

        for (Validator validator : validators) {
            Misfit misfit = validator.validate(maybeJson);

            if (misfit != null)
                return misfit;
        }

        return null;
    }

    public abstract ValidationMisfit validateType(JsonNode json);
    public abstract ValueType value(JsonNode json);

    public ThisType required() { validators.add(new RequiredValidator()); return (ThisType) this; }
    public ThisType custom(Validator validator) { validators.add(validator); return (ThisType) this; }

}
