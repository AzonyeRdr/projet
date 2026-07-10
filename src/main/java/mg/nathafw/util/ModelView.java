package mg.nathafw.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class ModelView {
    private final Map<String, Object> attributes;
    private String destination;

    public ModelView() {
        this(null);
    }

    public ModelView(String destination) {
        this.destination = destination;
        this.attributes = new LinkedHashMap<>();
    }

    public ModelView add(String key, Object value) {
        attributes.put(Objects.requireNonNull(key, "The attribute key must not be null"), value);
        return this;
    }

    public Object getValue(String key) {
        return attributes.get(key);
    }

    public Object remove(String key) {
        return attributes.remove(key);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
