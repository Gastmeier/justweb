package justweb.urlmapper;

import co.paralleluniverse.fibers.Suspendable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class UrlMappers implements UrlMapper {

    private List<UrlMapper> mappers = new ArrayList<>();

    public void add(UrlMapper mapper) { mappers.add(mapper); }

    @Suspendable
    public boolean handle(HttpServletRequest request, HttpServletResponse response) {
        for (UrlMapper mapper : mappers)
            if (mapper.handle(request, response)) {
                return true;
            }
        return false;
    }

}
