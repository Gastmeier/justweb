package justweb.pico;

import com.mitchellbosecke.pebble.PebbleEngine;
import justweb.Application;
import justweb.pebble.I18nFunction;
import org.picocontainer.injectors.Provider;

public class PebbleEngineProvider implements Provider {

    public PebbleEngine provide(Application app) {
        PebbleEngine pebble = new PebbleEngine();
        pebble.getFunctions().put("i18n", new I18nFunction(app.translations()));
        return pebble;
    }

}
