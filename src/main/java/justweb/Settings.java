package justweb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

public abstract class Settings<AppType extends Application> {

    protected final AppType app;
    private final Properties properties;

    public Settings(AppType app) {
        this.app = app;
        this.properties = new Properties();
    }

    public String serverHost() { return str("server.host", "localhost"); }
    public int serverPort() { return integer("server.port", 7070); }
    public int serverIdleTimeout() { return integer("server.idleTimeOut", 30000); }
    public String dbName() { return str("db.name", app.name()); }
    public boolean initHttpClient() { return bool("httpClient.init", false); }
    public String i18nBundle() { return str("i18n.bundle", "translations/translations"); }
    public String assetsPath() { return str("assets.path", "src/main/resources/assets"); }

    public void load(String propertiesFile) {
        try (InputStream stream = new FileInputStream(propertiesFile)) {
            properties.load(stream);
        } catch (FileNotFoundException e) {
            throw new AppException(String.format("Given settings file %s does not exist.", propertiesFile), e);
        } catch (IOException e) {
            throw new AppException(String.format("There was an IO error reading the settings file %s.", propertiesFile), e);
        }
    }

    public void loadOptional(String propertiesFile) {
        try (InputStream stream = new FileInputStream(propertiesFile)) {
            properties.load(stream);
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            throw new AppException(String.format("There was an IO error reading the settings file %s.", propertiesFile), e);
        }
    }

    protected String str(String property) {
        return required(property, value -> value);
    }

    protected String str(String property, String defaultValue) {
        return optional(property, defaultValue, value -> value);
    }

    protected boolean bool(String property) {
        return required(property, Boolean::parseBoolean);
    }

    protected boolean bool(String property, boolean defaultValue) {
        return optional(property, defaultValue, Boolean::parseBoolean);
    }

    protected int integer(String property) {
        return required(property, Integer::parseInt);
    }

    protected int integer(String property, int defaultValue) {
        return optional(property, defaultValue, Integer::parseInt);
    }

    protected <T> T required(String property, Function<String, T> convert) {
        String value = properties.getProperty(property);
        if (value == null)
            throw new AppException(String.format("Required configuration value for '%s' does not exist.", property));
        return convert.apply(value);
    }

    protected <T> T optional(String property, T defaultValue, Function<String, T> convert) {
        String value = properties.getProperty(property, null);
        if (value == null)
            return defaultValue;
        return convert.apply(value);
    }

}
