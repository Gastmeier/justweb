package justweb.jetty.client;

import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FiberHttpClient {

    private final HttpClient client;

    public FiberHttpClient(HttpClient client) {
        this.client = client;
    }

    public HttpClient jetty() { return client; }

    @Suspendable
    public ContentResponse send(Request request)
            throws InterruptedException, TimeoutException, ExecutionException {
        try {
            return new SendAsync(request).run();
        } catch (Throwable throwable) {
            if (throwable instanceof InterruptedException)
                throw (InterruptedException) throwable;
            if (throwable instanceof TimeoutException)
                throw (TimeoutException) throwable;
            if (throwable instanceof ExecutionException)
                throw (ExecutionException) throwable;

            throw new RuntimeException("Unexpected Exception: " + throwable.toString(), throwable);
        }
    }

    @Suspendable
    public ContentResponse get(String uri)
            throws InterruptedException, ExecutionException, TimeoutException {
        return send(client.newRequest(uri));
    }

    private class SendAsync extends FiberAsync<ContentResponse, Throwable>  {
        private final Request request;

        SendAsync(Request request) {
            this.request = request;
        }

        @Override
        protected void requestAsync() {
            request.send(new BufferingResponseListener() {
                @Override
                public void onComplete(Result result) {
                    if (result.isSucceeded()) {
                        ContentResponse response = new HttpContentResponse(result.getResponse(), getContent(),
                                getMediaType(), getEncoding());
                        asyncCompleted(response);
                    }
                    else {
                        asyncFailed(result.getFailure());
                    }
                }
            });
        }
    }

}
