package justweb.services;

import justweb.Settings;

public class UrlService {

    private final String baseUrl;
    private final String baseUrlSecure;

    public UrlService(Settings settings) {
        String port = "";
        if (settings.serverPort() != 80)
            port += ":" + String.valueOf(settings.serverPort());

        baseUrl = "http://" + settings.serverHost() + port;
        baseUrlSecure = "https://" + settings.serverHost() + port;
    }

    public String base(boolean secure) {
        return secure ? baseUrlSecure : baseUrl;
    }
}
