# KeyValueStore

Design a distributed key-value store that can perform the following

**Functional requirements**

1. Store a set of attributes (value) against a particular key (k)

2. Fetch the value stored against a particular key (k)

3. Delete a key (k)

4. Stretch - Perform a secondary index scan to fetch all key along with their
attributes where one of the attribute values is v.

!["Key Values"](images/key-value-store.png?raw=true)

Key can have a value consisting of multiple attributes.

Each attribute will have name, type associated (primitive types - boolean, double, integer, string) & type has to be identified at run time.

1) Key = delhi has 2 attributes only (pollution_level & population)
2) Key = jakarta has 3 attributes (latitude, longitude, pollution_level)
3) Key = bangalore has 4 attributes (extra - free_food)
4) Key = india has 2 attributes (capital & population)
5) Key = crocin has 2 attributes (category & manufacturer)

### Example of Secondary index:

Get all keys (cities) where pollution_level is high.

Get all medicines by manufacturer (GSK)

So, in a nutshell, value must be strongly typed when defined.

### Attribute
1. Attribute is uniquely identified by its name (latitude, longitude etc.

2. Data type of the attribute is defined at the first insert. (i.e. data type of pollution_level is set when key = delhi is inserted)

3. Once data type is associated with a particular attribute, it cannot be changed.
(i.e. free_food when defined takes type = boolean, hence, any key when using the attribute - free_food must allow only boolean values on subsequent inserts/updates)

### KeyValueStore
```java
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
```
