package justweb.urlmapper;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableCallable;
import co.paralleluniverse.strands.SuspendableRunnable;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ExecutionException;

public abstract class RestUrlMapper implements UrlMapper {

    protected final ObjectMapper jackson;

    public RestUrlMapper(ObjectMapper jackson) {
        this.jackson = jackson;
    }

    @Override
    @Suspendable
    public abstract boolean handle(HttpServletRequest request, HttpServletResponse response);

    @Suspendable
    protected <T> T async(SuspendableCallable<T> callable) {
        T result = null;

        try {
            result = new Fiber<T>() {
                @Override
                protected T run() throws SuspendExecution, InterruptedException {
                    return callable.run();
                }
            }.start().get();
        } catch (ExecutionException e) {
            e.printStackTrace(); // TODO: do something meaningful
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO: do something meaningful
        }

        return result;
    }

    @Suspendable
    protected void asyncVoid(SuspendableRunnable runnable) {
        try {
            new Fiber<Void>() {
                @Override
                protected Void run() throws SuspendExecution, InterruptedException {
                    runnable.run();
                    return null;
                }
            }.start().get();
        } catch (ExecutionException e) {
            e.printStackTrace(); // TODO: do something meaningful
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO: do something meaningful
        }
    }

    protected Reader reader(HttpServletRequest request) {
        Reader reader = null;
        try {
            reader = request.getReader();
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: do something meaningful
        }
        return reader;
    }

    protected <T> T jsonRequest(HttpServletRequest request, Class<T> valueType) {
        T result = null;

        try {
            Reader reader = request.getReader();
            result = jackson.readValue(reader, valueType);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: do something meaningful
        }

        return result;
    }

    protected boolean jsonResponse(HttpServletResponse response, Object json) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ServletOutputStream out = response.getOutputStream();
            jackson.writeValue(out, json);
        } catch (IOException e) {
            e.printStackTrace();
            return false; // TODO: do something useful here
        }

        return true; // TODO: really return true all the times?
    }

}
