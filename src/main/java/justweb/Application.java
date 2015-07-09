package justweb;

import justweb.jetty.client.FiberHttpClient;
import justweb.jetty.server.JettyHandler;
import justweb.pico.*;
import justweb.routing.Routes;
import justweb.services.EmailService;
import justweb.services.MongoService;
import justweb.services.PebbleService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.injectors.ProviderAdapter;

public abstract class Application {

    private final Settings settings = new Settings(appName());

    public static MutablePicoContainer PICO = new DefaultPicoContainer(new Caching(), null);

    public void loadSettings() {
        settings.load("src/main/resources/" + appName().toLowerCase() + ".properties");
        settings.loadOptional("src/main/resources/" + appName().toLowerCase() + "_local.properties");
    }

    public void initServices() {
        PICO.addComponent(settings);
        PICO.addComponent(JettyHandler.class);
        PICO.addComponent(Routes.class);
        PICO.addAdapter(new ProviderAdapter(new MongoClientProvider()));
        PICO.addAdapter(new ProviderAdapter(new MongoDatabaseProvider()));
        PICO.addComponent(MongoService.class);
        PICO.addAdapter(new ProviderAdapter(new PebbleEngineProvider()));
        PICO.addComponent(PebbleService.class);
        PICO.addAdapter(new ProviderAdapter(new JsonMapperProvider()));
        PICO.addComponent(EmailService.class);
        PICO.addAdapter(new ProviderAdapter(new I18nProvider()));


        if (settings.initHttpClient()) {
            PICO.addAdapter(new ProviderAdapter(new HttpClientProvider()));
            PICO.addComponent(FiberHttpClient.class);
        }
    }

    public void run() {
        loadSettings();
        initServices();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(settings.serverHost());
        connector.setPort(settings.serverPort());
        connector.setIdleTimeout(settings.serverIdleTimeout());
        server.addConnector(connector);

        JettyHandler appHandler = PICO.getComponent(JettyHandler.class);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(settings.assetsPath());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{appHandler, resourceHandler});

        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract String appName();

}
