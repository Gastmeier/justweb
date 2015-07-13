package justweb;

import org.eclipse.jetty.server.Server;

public abstract class Application<SettingsType extends Settings, RegistryType extends Registry> {

    public final SettingsType settings = newSettings();
    public final RegistryType registry = newRegistry(settings);

    protected abstract SettingsType newSettings();
    protected abstract RegistryType newRegistry(SettingsType settings);

    public void loadSettings() {
        settings.load("src/main/resources/" + settings.appName().toLowerCase() + ".properties");
        settings.loadOptional("src/main/resources/" + settings.appName().toLowerCase() + "_local.properties");
    }

    public void run() {
        loadSettings();
        Server server = registry.jettyServer();

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
