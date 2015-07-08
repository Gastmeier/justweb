package justweb.pebble;

import java.util.Map;

public interface PebbleContext {

    String template();
    Map<String, Object> map();

}
