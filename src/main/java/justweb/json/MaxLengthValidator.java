package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class MaxLengthValidator extends Validator {

    private final StringTooLongMisfit error = new StringTooLongMisfit(this);
    private final int max;

    public MaxLengthValidator(int max) {
        this.max = max;
    }

    @Override
    public ValidationMisfit validate(Optional<JsonNode> maybeNode) {
        if (maybeNode.isPresent()) {
            JsonNode node = maybeNode.get();

            if (node.isNull())
                return null;

            if (node.asText().length() > max)
                return error;
        }

        return null;
    }

    public static class StringTooLongMisfit extends ValidationMisfit<MaxLengthValidator> {
        public StringTooLongMisfit(MaxLengthValidator validator) {
            super(validator, "error.string_too_long");
        }

        @Override
        public String getMessage(ResourceBundle messages) {
            String message = messages.getString(getCode());
            return MessageFormat.format(message, getValidator().max);
        }
    }
}
