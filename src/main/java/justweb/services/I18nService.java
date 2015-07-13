package justweb.services;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nService {

    private final String bundle;

    public I18nService(String bundle) {
        this.bundle = bundle;
    }

    public ResourceBundle get(Locale language) { return ResourceBundle.getBundle(bundle, language); }
    public String trans(Locale language, String key) { return get(language).getString(key); }
    public String trans(HttpServletRequest request, String key) { return get(lang(request)).getString(key); }
    public Locale lang(HttpServletRequest request) { return request.getLocale(); }

    public Bound bind(Locale lang) { return new Bound(lang); }
    public Bound bind(HttpServletRequest request) { return new Bound(request); }

    public class Bound {
        public final Locale lang;

        private Bound(Locale lang) {
            this.lang = lang;
        }

        private Bound(HttpServletRequest request) {
            this(I18nService.this.lang(request));
        }

        public ResourceBundle get() { return I18nService.this.get(lang); }
        public String trans(String key) { return I18nService.this.trans(lang, key); }
    }

}
