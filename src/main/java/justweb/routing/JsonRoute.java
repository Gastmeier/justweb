package justweb.routing;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class JsonRoute implements Route {
    protected final ObjectMapper jsonMapper;

    protected JsonRoute(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    @Suspendable
    public void handle(HttpServletRequest request, HttpServletResponse response) throws SuspendExecution {
        try {
            JsonNode json = handleJson(request, response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ServletOutputStream out = response.getOutputStream();
            jsonMapper.writeValue(out, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Suspendable
    protected abstract JsonNode handleJson(HttpServletRequest request, HttpServletResponse response);

}
