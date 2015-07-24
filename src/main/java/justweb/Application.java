package justweb;

import org.eclipse.jetty.server.Server;

public abstract class Application<SettingsType extends Settings, RegistryType extends Registry> {

    public final SettingsType settings = newSettings();
    public final RegistryType registry = newRegistry();

    public abstract String name();
    protected abstract SettingsType newSettings();
    protected abstract RegistryType newRegistry();

    protected void init() {
        loadSettings();
    }

    protected void loadSettings() {
        settings.load("src/main/resources/" + name().toLowerCase() + ".properties");
        settings.loadOptional("src/main/resources/" + name().toLowerCase() + "_local.properties");
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
