package justweb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import justweb.jetty.client.FiberJettyHttpClient;
import justweb.jetty.server.JettyHandler;
import justweb.services.EmailService;
import justweb.services.I18nService;
import justweb.services.MongoService;
import justweb.services.PebbleService;
import justweb.urlmapper.UrlMapper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public abstract class Registry<SettingsType extends Settings> {

    private final SettingsType settings;

    private Server jettyServer;
    private JettyHandler jettyHandler;
    private I18nService i18nService;
    private EmailService emailService;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoService mongoService;
    private PebbleEngine pebbleEngine;
    private PebbleService pebbleService;
    private ObjectMapper jacksonMapper;
    private HttpClient jettyHttpClient;
    private FiberJettyHttpClient fiberJettyHttpClient;

    public Registry(SettingsType settings) {
        this.settings = settings;
    }

    public Server jettyServer() {
        if (jettyServer == null) {
            jettyServer = new Server();
            ServerConnector connector = new ServerConnector(jettyServer);
            connector.setHost(settings.serverHost());
            connector.setPort(settings.serverPort());
            connector.setIdleTimeout(settings.serverIdleTimeout());
            jettyServer.addConnector(connector);

            JettyHandler appHandler = jettyHandler();

            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase(settings.assetsPath());

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{appHandler, resourceHandler});

            jettyServer.setHandler(handlers);
        }

        return jettyServer;
    }

    public abstract UrlMapper urlMapper();

    public JettyHandler jettyHandler() {
        if (jettyHandler == null) {
            jettyHandler = new JettyHandler(urlMapper());
        }

        return jettyHandler;
    }

    public I18nService i18nService() {
        if (i18nService == null) {
            i18nService = new I18nService(settings.i18nBundle());
        }

        return i18nService;
    }

    public EmailService emailService() {
        if (emailService == null) {
            emailService = new EmailService();
        }

        return emailService;
    }

    public MongoClient mongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create();
        }

        return mongoClient;
    }

    public MongoDatabase mongoDatabase() {
        if (mongoDatabase == null) {
            mongoDatabase = mongoClient().getDatabase(settings.dbName());
        }

        return mongoDatabase;
    }

    public MongoService mongoService() {
        if (mongoService == null) {
            mongoService = new MongoService(mongoDatabase());
        }

        return mongoService;
    }

    public PebbleEngine pebbleEngine() {
        if (pebbleEngine == null) {
            pebbleEngine = new PebbleEngine();
        }

        return pebbleEngine;
    }

    public PebbleService pebbleService() {
        if (pebbleService == null) {
            pebbleService = new PebbleService(pebbleEngine(), i18nService());
        }

        return pebbleService;
    }

    public ObjectMapper jacksonMapper() {
        if (jacksonMapper == null) {
            jacksonMapper = new ObjectMapper();
            jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jacksonMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        }

        return jacksonMapper;
    }

    public HttpClient jettyHttpClient() {
        if (jettyHttpClient == null) {
            jettyHttpClient = new HttpClient();

            try {
                jettyHttpClient.start();
            } catch (Exception e) {
                throw new RuntimeException(e); // TODO: what to do here?
            }
        }

        return jettyHttpClient;
    }

    public FiberJettyHttpClient fiberJettyHttpClient() {
        if (fiberJettyHttpClient == null) {
            fiberJettyHttpClient = new FiberJettyHttpClient(jettyHttpClient());
        }

        return fiberJettyHttpClient;
    }

}
