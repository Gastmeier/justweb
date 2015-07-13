package justweb.urlmapper;

import co.paralleluniverse.fibers.SuspendExecution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class UrlMappers {

    private List<UrlMapper> mappers = new ArrayList<>();

    public void add(UrlMapper mapper) { mappers.add(mapper); }

    public boolean handle(HttpServletRequest request, HttpServletResponse response)
            throws SuspendExecution {
        for (UrlMapper mapper : mappers)
            if (mapper.handle(request, response)) {
                return true;
            }
        return false;
    }

}
