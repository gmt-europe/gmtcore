package nl.gmt.xml;

import java.util.List;

public class ChildNodeA {
    private final String attr1;
    private final String attr2;
    private final List<ChildSubNode> subNodes;

    public ChildNodeA(String attr1, String attr2, List<ChildSubNode> subNodes) {
        this.attr1 = attr1;
        this.attr2 = attr2;
        this.subNodes = subNodes;
    }

    public String getAttr1() {
        return attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public List<ChildSubNode> getSubNodes() {
        return subNodes;
    }
}
