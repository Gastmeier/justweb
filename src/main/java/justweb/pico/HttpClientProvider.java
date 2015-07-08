package justweb.pico;

import org.eclipse.jetty.client.HttpClient;
import org.picocontainer.injectors.Provider;

public class HttpClientProvider implements Provider {

    public HttpClient provide() {
        HttpClient httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: what to do here?
        }

        return httpClient;
    }

}
