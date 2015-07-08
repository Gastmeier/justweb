package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Optional;

public class RequiredValidator extends Validator {

    private final MissingMisfit error = new MissingMisfit(this);

    @Override
    public ValidationMisfit validate(Optional<JsonNode> maybeNode) {
        if (! maybeNode.isPresent() || maybeNode.get().isNull())
            return error;

        if (maybeNode.get().getNodeType() == JsonNodeType.STRING) {
            String value = maybeNode.get().asText();

            if (value == null || value.isEmpty())
                return error;
        }

        return null;
    }

    public static class MissingMisfit extends ValidationMisfit<RequiredValidator> {
        public MissingMisfit(RequiredValidator validator) {
            super(validator, "error.missing");
        }
    }

}
