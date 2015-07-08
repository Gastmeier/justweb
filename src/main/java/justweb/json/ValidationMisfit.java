package justweb.json;

import justweb.Misfit;

public abstract class ValidationMisfit<ValidatorType extends Validator> extends Misfit {

    private final ValidatorType validator;

    public ValidationMisfit(ValidatorType validator, String messageKey) {
        super(messageKey);
        this.validator = validator;
    }

    public ValidatorType getValidator() { return validator; }

}
