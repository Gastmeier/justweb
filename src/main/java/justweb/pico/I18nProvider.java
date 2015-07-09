package justweb.pico;

import justweb.Settings;
import justweb.services.I18nService;
import org.picocontainer.injectors.Provider;

public class I18nProvider implements Provider {

    public I18nService provide(Settings settings) {
        return new I18nService(settings.i18nBundle());
    }

}
