package net.mahtbalam;

public class Attribute {
    private final String name;
    private final AttributeType type;
    private final Object value;

    public Attribute(String name, AttributeType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "(" + type + ")=" + value;
    }
}