package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class MinLengthValidator extends Validator {

    private final StringTooShortMisfit error = new StringTooShortMisfit(this);
    private final int min;

    public MinLengthValidator(int min) {
        this.min = min;
    }

    @Override
    public ValidationMisfit validate(Optional<JsonNode> maybeNode) {
        if (maybeNode.isPresent()) {
            JsonNode node = maybeNode.get();

            if (node.isNull())
                return null;

            if (node.asText().length() < min)
                return error;
        }

        return null;
    }

    public static class StringTooShortMisfit extends ValidationMisfit<MinLengthValidator> {
        public StringTooShortMisfit(MinLengthValidator validator) {
            super(validator, "error.string_too_short");
        }

        @Override
        public String getMessage(ResourceBundle messages) {
            String message = messages.getString(getCode());
            return MessageFormat.format(message, getValidator().min);
        }
    }
}
