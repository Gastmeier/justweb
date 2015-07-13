package justweb.jetty.server;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import justweb.urlmapper.UrlMappers;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class JettyHandler extends AbstractHandler {

    private final UrlMappers urlMappers;

    public JettyHandler(UrlMappers urlMappers) {
        this.urlMappers = urlMappers;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            Fiber<Boolean> fiber = new Fiber<Boolean>() {
                @Override
                protected Boolean run() throws SuspendExecution, InterruptedException {
                    return urlMappers.handle(request, response);
                }
            }.start();

            Boolean handled = fiber.get();
            baseRequest.setHandled(handled);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
