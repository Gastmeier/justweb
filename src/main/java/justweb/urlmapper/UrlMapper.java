package justweb.urlmapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UrlMapper {

    boolean handle(HttpServletRequest request, HttpServletResponse response);

}
