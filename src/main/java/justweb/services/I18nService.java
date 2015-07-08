package justweb.services;

import justweb.Application;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nService {

    private final Application app;

    public I18nService(Application app) {
        this.app = app;
    }

    public ResourceBundle get(Locale language) { return ResourceBundle.getBundle(app.translations(), language); }
    public String trans(Locale language, String key) { return get(language).getString(key); }
    public Locale lang(HttpServletRequest request) { return request.getLocale(); }

    public Translator translator(HttpServletRequest request) {
        return new Translator(request);
    }

    public class Translator {
        private final Locale lang;

        public Translator(HttpServletRequest request) {
            this.lang = I18nService.this.lang(request);
        }

        public Locale lang() { return lang; }
        public ResourceBundle get() { return I18nService.this.get(lang); }
        public String trans(String key) { return I18nService.this.trans(lang, key); }
    }

}
