package justweb;

import org.eclipse.jetty.server.Server;

public abstract class Application<SettingsType extends Settings, RegistryType extends Registry> {

    public final SettingsType settings = newSettings();
    public final RegistryType registry = newRegistry(settings);

    private String baseUrl;
    private String baseUrlSecure;

    protected abstract SettingsType newSettings();
    protected abstract RegistryType newRegistry(SettingsType settings);

    public String baseUrl(boolean secure) {
        return secure ? baseUrlSecure : baseUrl;
    }

    protected void init() {
        loadSettings();

        String port = "";
        if (settings.serverPort() != 80)
            baseUrl += ":" + String.valueOf(settings.serverPort());

        baseUrl = "http://" + settings.serverHost() + port;
        baseUrlSecure = "https://" + settings.serverHost() + port;
    }

    protected void loadSettings() {
        settings.load("src/main/resources/" + settings.appName().toLowerCase() + ".properties");
        settings.loadOptional("src/main/resources/" + settings.appName().toLowerCase() + "_local.properties");
    }

    public void run() {
        init();
        Server server = registry.jettyServer();

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO: what to do here?
        } catch (Exception e) {
            e.printStackTrace(); // TODO: what to do here?
        }
    }

}
