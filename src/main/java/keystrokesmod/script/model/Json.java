package keystrokesmod.script.model;

import com.google.gson.*;
import java.util.*;

public class Json {
    public enum Type { OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL }

    private final JsonElement element;
    private final Type type;

    public Json(JsonElement element) {
        this.element = element == null ? JsonNull.INSTANCE : element;
        if (this.element.isJsonObject()) {
            this.type = Type.OBJECT;
        }
        else if (this.element.isJsonArray()) {
            this.type = Type.ARRAY;
        }
        else if (this.element.isJsonPrimitive()) {
            JsonPrimitive prim = this.element.getAsJsonPrimitive();
            if (prim.isBoolean()) {
                this.type = Type.BOOLEAN;
            }
            else if (prim.isNumber()) {
                this.type = Type.NUMBER;
            }
            else {
                this.type = Type.STRING;
            }
        }
        else {
            this.type = Type.NULL;
        }
    }

    public Json(String jsonString) {
        this(parseElement(jsonString));
    }

    private static JsonElement parseElement(String jsonString) {
        try {
            return new JsonParser().parse(jsonString);
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    public static Json parse(String jsonString) {
        return new Json(parseElement(jsonString));
    }

    public Json object() {
        if (type == Type.OBJECT) {
            return this;
        }
        return new Json(new JsonObject());
    }

    public Json object(String key) {
        return get(key);
    }

    public List<Json> array() {
        return asArray();
    }

    public List<Json> array(String key) {
        return get(key).asArray();
    }

    public static Json string(String value) {
        return new Json(new JsonPrimitive(value));
    }

    public static Json number(Number value) {
        return new Json(new JsonPrimitive(value));
    }

    public static Json booleanValue(boolean value) {
        return new Json(new JsonPrimitive(value));
    }

    public static Json nullValue() {
        return new Json(JsonNull.INSTANCE);
    }

    public Type type() {
        return type;
    }

    private void ensureObject() {
        if (type != Type.OBJECT) {
            throw new IllegalStateException("Not a JSON object: " + type);
        }
    }

    public Map<String, Json> map() {
        if (type != Type.OBJECT || !element.isJsonObject()) {
            return new LinkedHashMap<>();
        }
        Map<String, Json> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
            map.put(e.getKey(), new Json(e.getValue()));
        }
        return map;
    }

    public Json add(String key, Json value) {
        ensureObject();
        this.element.getAsJsonObject().add(key, value.element);
        return this;
    }

    public Json add(String key, String  val) {
        return add(key, Json.string(val));
    }

    public Json add(String key, Number  val) {
        return add(key, Json.number(val));
    }

    public Json add(String key, boolean val) {
        return add(key, Json.booleanValue(val));
    }

    public Json get(String key) {
        if (type != Type.OBJECT || !element.isJsonObject()) {
            return Json.nullValue();
        }
        JsonElement child = this.element.getAsJsonObject().get(key);
        return child == null ? Json.nullValue() : new Json(child);
    }

    public String get(String key, String defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asString();
    }

    public int get(String key, int defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asInt();
    }

    public double get(String key, double defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asDouble();
    }

    public float get(String key, float defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asFloat();
    }

    public long get(String key, long defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asLong();
    }

    public boolean get(String key, boolean defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val.asBoolean();
    }

    public Json get(String key, Json defaultValue) {
        Json val = get(key);
        return val.type() == Type.NULL ? defaultValue : val;
    }

    public boolean has(String key) {
        if (type != Type.OBJECT || !element.isJsonObject()) {
            return false;
        }
        return this.element.getAsJsonObject().has(key);
    }

    private void ensureArray() {
        if (type != Type.ARRAY) {
            throw new IllegalStateException("Not a JSON array: " + type);
        }
    }

    public Json add(Json value) {
        ensureArray();
        this.element.getAsJsonArray().add(value.element);
        return this;
    }

    public Json add(String  val) {
        return add(Json.string(val));
    }

    public Json add(Number  val) {
        return add(Json.number(val));
    }

    public Json add(boolean val) {
        return add(Json.booleanValue(val));
    }

    public List<Json> asArray() {
        if (type != Type.ARRAY || !element.isJsonArray()) {
            return new ArrayList<>();
        }
        List<Json> list = new ArrayList<>();
        for (JsonElement el : this.element.getAsJsonArray()) {
            list.add(new Json(el));
        }
        return list;
    }

    public String asString() {
        if (element == null || element.isJsonNull()) {
            return "";
        }
        try {
            return element.getAsString();
        } catch (Exception e) {
            return element.toString();
        }
    }

    public int asInt() {
        try {
            return element.getAsInt();
        } catch (Exception e) {
            return 0;
        }
    }

    public double asDouble() {
        try {
            return element.getAsDouble();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public long asLong() {
        try {
            return element.getAsLong();
        } catch (Exception e) {
            return 0L;
        }
    }

    public float asFloat() {
        try {
            return element.getAsFloat();
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public boolean asBoolean() {
        try {
            return element.getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public LinkedHashSet<String> keys() {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        if (type != Type.OBJECT || !element.isJsonObject()) {
            return out;
        }
        for (Map.Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
            out.add(e.getKey());
        }
        return out;
    }

    public Json remove(String key) {
        if (type == Type.OBJECT && element.isJsonObject()) {
            element.getAsJsonObject().remove(key);
        }
        return this;
    }

    public Json remove(final int index) {
        if (type != Type.ARRAY || !element.isJsonArray() || index < 0) {
            return this;
        }

        JsonArray arr = element.getAsJsonArray();
        int i = 0;

        for (Iterator<JsonElement> it = arr.iterator(); it.hasNext(); i++) {
            it.next();
            if (i == index) {
                it.remove();
                break;
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}