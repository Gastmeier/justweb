package justweb;

import java.util.ResourceBundle;

public abstract class Misfit {

    private final String code;

    public Misfit(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
    public String getMessage(ResourceBundle messages) {
        return messages.getString(getCode());
    }

}
