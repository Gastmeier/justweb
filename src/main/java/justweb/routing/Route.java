package justweb.routing;

import co.paralleluniverse.fibers.SuspendExecution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Route {

    default boolean applies(HttpServletRequest request) {
        return applies(request.getMethod(), request.getPathInfo());
    }

    default boolean applies(String method, String path) {
        return false;
    }

    void handle(HttpServletRequest request, HttpServletResponse response) throws SuspendExecution ;

}
