package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import justweb.Misfit;

import java.util.Optional;

public abstract class Validator {

    public abstract Misfit validate(Optional<JsonNode> maybeJson);

}
