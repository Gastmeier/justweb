package justweb.json;

import com.fasterxml.jackson.databind.JsonNode;
import justweb.Misfit;
import justweb.services.I18nService;

import java.util.*;

public class JsonSchema {

    protected final List<JsonField> fields = new ArrayList<JsonField>(1);
    protected final I18nService i18n;

    public JsonSchema(I18nService i18nService) {
        this.i18n = i18nService;
    }

    public StringField _string(String name, List<JsonField> fields) {
        StringField field = new StringField(name);
        fields.add(field);
        return field;
    }

    public LongField _long(String name, List<JsonField> fields) {
        LongField field = new LongField(name);
        fields.add(field);
        return field;
    }

    public StringField _string(String name) {
        return _string(name, fields());
    }
    public LongField _long(String name) { return _long(name, fields()); }

    public List<JsonField> fields() {
        return fields;
    }

    protected Map<String, Misfit> collectMisfits(JsonNode json, List<JsonField> fields) {
        Map<String, Misfit> misfits = null;

        for (JsonField field : fields) {
            Misfit misfit = field.validate(Optional.ofNullable(json.get(field.getName())));

            if (misfit != null) {
                if (misfits == null) misfits = new HashMap<>(1, 1);
                misfits.put(field.getName(), misfit);
            }
        }

        return mapOrNull(misfits);
    }

    protected Map<String, Misfit> collectMisfits(JsonNode json) {
        return collectMisfits(json, fields());
    }

    protected Misfit findFirstMisfit(JsonNode json, List<JsonField> fields) {
        for (JsonField field : fields) {
            Misfit misfit = field.validate(Optional.ofNullable(json.get(field.getName())));

            if (misfit != null)
                return misfit;
        }

        return null;
    }

    protected Misfit findFirstMisfit(JsonNode json) {
        return findFirstMisfit(json, fields());
    }

    protected boolean contains(Map<String, Misfit> misfits, String key) {
        return misfits != null && misfits.containsKey(key);
    }

    protected Map<String, Misfit> init(Map<String, Misfit> misifits) {
        if (misifits == null)
            return new HashMap<String, Misfit>(1, 1);
        return misifits;
    }

    protected Map<String, Misfit> mapOrNull(Map<String, Misfit> misfits) {
        if (misfits == null || misfits.isEmpty())
            return null;
        return misfits;
    }

}
