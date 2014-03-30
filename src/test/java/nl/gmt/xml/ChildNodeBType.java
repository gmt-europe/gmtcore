package nl.gmt.xml;

public enum ChildNodeBType implements AttributeEnum {
    A("A"),
    B("B");

    private final String value;

    private ChildNodeBType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
