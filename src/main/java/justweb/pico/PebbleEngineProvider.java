package justweb.pico;

import com.mitchellbosecke.pebble.PebbleEngine;
import justweb.Settings;
import justweb.pebble.I18nFunction;
import org.picocontainer.injectors.Provider;

public class PebbleEngineProvider implements Provider {

    public PebbleEngine provide(Settings settings) {
        PebbleEngine pebble = new PebbleEngine();
        pebble.getFunctions().put("i18n", new I18nFunction(settings.i18nBundle()));
        return pebble;
    }

}
