package justweb.services;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import justweb.pebble.PebbleContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class PebbleService {

    private final PebbleEngine pebble;
    private final I18nService i18n;

    public PebbleService(PebbleEngine pebble, I18nService i18n) {
        this.pebble = pebble;
        this.i18n = i18n;
    }

    public void render(HttpServletRequest request, HttpServletResponse response, PebbleContext context) {
        PebbleTemplate t = null;
        try {
            t = pebble.getTemplate(context.template());
        } catch (PebbleException e) {
            e.printStackTrace();
        }

        Writer w = null;
        try {
            w = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Locale lang = i18n.lang(request);

        try {
            t.evaluate(w, context.map(), lang);
        } catch (PebbleException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
