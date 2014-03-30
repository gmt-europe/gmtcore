package nl.gmt.xml;

public class ChildSubNode {
    private final String attr1;
    private final String value;

    public ChildSubNode(String attr1, String value) {
        this.attr1 = attr1;
        this.value = value;
    }

    public String getAttr1() {
        return attr1;
    }

    public String getValue() {
        return value;
    }
}
