package justweb;

import java.util.ResourceBundle;

public class MisfitException extends RuntimeException {

    private final Misfit misfit;

    public MisfitException(Misfit misfit, ResourceBundle messages) {
        super(misfit.getMessage(messages));
        this.misfit = misfit;
    }
}
