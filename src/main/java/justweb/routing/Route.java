package justweb.routing;

import co.paralleluniverse.fibers.SuspendExecution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Route {

    boolean applies(HttpServletRequest request);
    void handle(HttpServletRequest request, HttpServletResponse response) throws SuspendExecution ;

}
