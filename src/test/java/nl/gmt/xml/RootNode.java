package nl.gmt.xml;

import java.util.List;

public class RootNode {
    private final ChildNodeA nodeA;
    private final List<ChildNodeB> nodeBs;

    public RootNode(ChildNodeA nodeA, List<ChildNodeB> nodeBs) {
        this.nodeA = nodeA;
        this.nodeBs = nodeBs;
    }

    public ChildNodeA getNodeA() {
        return nodeA;
    }

    public List<ChildNodeB> getNodeBs() {
        return nodeBs;
    }
}
