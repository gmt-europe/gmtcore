package nl.gmt.xml;

public class ChildNodeB {
    private final ChildNodeBType type;
    private final ChildSubNode subNode;

    public ChildNodeB(ChildNodeBType type, ChildSubNode subNode) {
        this.type = type;
        this.subNode = subNode;
    }

    public ChildNodeBType getType() {
        return type;
    }

    public ChildSubNode getSubNode() {
        return subNode;
    }
}
