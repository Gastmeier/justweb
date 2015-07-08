package justweb.pebble;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;

import java.util.*;

public class I18nFunction implements Function {

    private final String bundleBaseName;
    private final List<String> argumentNames = new ArrayList<>();

    public I18nFunction(String bundleBaseName) {
        this.bundleBaseName = bundleBaseName;
        argumentNames.add("key");
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String key = (String) args.get("key");

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();

        ResourceBundle bundle = ResourceBundle.getBundle(bundleBaseName, locale);

        return bundle.getObject(key);
    }

}
