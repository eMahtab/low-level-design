package net.mahtbalam;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private final ConcurrentHashMap<String, Map<String, Attribute>> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AttributeType> attributeTypes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Map<Object, Set<String>>> secondaryIndex = new ConcurrentHashMap<>();

    // Insert or update key-value pair
    public void put(String key, Map<String, Object> attributes) {
        store.putIfAbsent(key, new ConcurrentHashMap<>());
        Map<String, Attribute> existingAttributes = store.get(key);

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();

            // Determine attribute type on first insertion
            AttributeType type = determineType(attrValue);

            // Enforce type consistency
            attributeTypes.putIfAbsent(attrName, type);
            if (attributeTypes.get(attrName) != type) {
                throw new IllegalArgumentException("Type mismatch for attribute: " + attrName);
            }

            // Update value
            Attribute attribute = new Attribute(attrName, type, attrValue);
            existingAttributes.put(attrName, attribute);

            // Update secondary index
            secondaryIndex.putIfAbsent(attrName, new ConcurrentHashMap<>());
            secondaryIndex.get(attrName).putIfAbsent(attrValue, ConcurrentHashMap.<String>newKeySet());
            secondaryIndex.get(attrName).get(attrValue).add(key);
        }
    }

    // Fetch value by key
    public Map<String, Attribute> get(String key) {
        return store.getOrDefault(key, Collections.<String, Attribute>emptyMap());
    }

    // Delete a key
    public void delete(String key) {
        Map<String, Attribute> attributes = store.remove(key);
        if (attributes != null) {
            for (Attribute attr : attributes.values()) {
                secondaryIndex.getOrDefault(attr.getName(), Collections.<Object, Set<String>>emptyMap())
                        .getOrDefault(attr.getValue(), Collections.<String>emptySet())
                        .remove(key);
            }
        }
    }

    // Secondary index lookup
    public Set<String> getByAttributeValue(String attribute, Object value) {
        return secondaryIndex.getOrDefault(attribute, Collections.<Object, Set<String>>emptyMap())
                .getOrDefault(value, Collections.emptySet());
    }

    // Determine attribute type at runtime
    private AttributeType determineType(Object value) {
        if (value instanceof String) return AttributeType.STRING;
        if (value instanceof Integer) return AttributeType.INTEGER;
        if (value instanceof Double) return AttributeType.DOUBLE;
        if (value instanceof Boolean) return AttributeType.BOOLEAN;
        throw new IllegalArgumentException("Unsupported data type");
    }
}
