package justweb;

import justweb.jetty.client.FiberHttpClient;
import justweb.jetty.server.JettyHandler;
import justweb.pico.HttpClientProvider;
import justweb.pico.JsonMapperProvider;
import justweb.pico.PebbleEngineProvider;
import justweb.routing.Routes;
import justweb.services.EmailService;
import justweb.services.I18nService;
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

public class Application {

    public static MutablePicoContainer PICO = new DefaultPicoContainer(new Caching(), null);

    public void initServices() {
        PICO.addComponent(this);
        PICO.addComponent(JettyHandler.class);
        PICO.addComponent(Routes.class);
        PICO.addAdapter(new ProviderAdapter(new PebbleEngineProvider()));
        PICO.addComponent(PebbleService.class);
        PICO.addAdapter(new ProviderAdapter(new JsonMapperProvider()));
        PICO.addComponent(EmailService.class);
        PICO.addComponent(I18nService.class);

        if (initHttpClient()) {
            PICO.addAdapter(new ProviderAdapter(new HttpClientProvider()));
            PICO.addComponent(FiberHttpClient.class);
        }
    }

    public void run() {
        initServices();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("localhost");
        connector.setPort(7070);
        connector.setIdleTimeout(30000);
        server.addConnector(connector);

        JettyHandler appHandler = PICO.getComponent(JettyHandler.class);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(assetsPath());

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

    public boolean initHttpClient() { return false; }
    public String translations() { return "translations/translations"; }
    public String assetsPath() { return "src/main/resources/assets"; }

}
