package justweb.routing;

import co.paralleluniverse.fibers.SuspendExecution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Routes {

    private List<Route> routes = new ArrayList<>();

    public void add(Route route) { routes.add(route); }

    public boolean handle(HttpServletRequest request, HttpServletResponse response)
            throws SuspendExecution {
        for (Route route : routes)
            if (route.applies(request)) {
                route.handle(request, response);
                return true;
            }
        return false;
    }

}
