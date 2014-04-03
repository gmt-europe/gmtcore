package nl.gmt.xml;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TestReader extends XMLReader<RootNode> {
    public TestReader(InputStream is) throws XMLStreamException {
        super(is);
    }

    public TestReader(Reader reader) throws XMLStreamException {
        super(reader);
    }

    public RootNode read() throws XMLStreamException {
        nextElement("root");

        ChildNodeA childNodeA = null;
        List<ChildNodeB> childNodeBs = null;

        while (nextChild()) {
            switch (getLocalName()) {
                case "childNodeA":
                    childNodeA = readChildNodeA();
                    break;
                case "childNodeBs":
                    childNodeBs = readChildNodeBs();
                    break;
                default:
                    throw illegalState();
            }
        }

        return new RootNode(childNodeA, childNodeBs);
    }

    private List<ChildNodeB> readChildNodeBs() throws XMLStreamException {
        List<ChildNodeB> result = new ArrayList<>();

        while (nextChild()) {
            switch (getLocalName()) {
                case "childNodeB": result.add(readChildNodeB()); break;
                default: throw illegalState();
            }
        }

        return result;
    }

    private ChildNodeA readChildNodeA() throws XMLStreamException {
        String attr1 = null;
        String attr2 = null;
        List<ChildSubNode> subNodes = null;

        while (nextAttribute()) {
            switch (getAttributeLocalName()) {
                case "attr1":
                    attr1 = getAttributeString();
                    break;
                case "attr2":
                    attr2 = getAttributeString();
                    break;
                default:
                    throw illegalState();
            }
        }

        while (nextChild()) {
            switch (getLocalName()) {
                case "subNodes":
                    subNodes = readSubNodes();
                    break;
                default:
                    throw illegalState();
            }
        }

        return new ChildNodeA(attr1, attr2, subNodes);
    }

    private List<ChildSubNode> readSubNodes() throws XMLStreamException {
        List<ChildSubNode> result = new ArrayList<>();

        while (nextChild()) {
            switch (getLocalName()) {
                case "subNode": result.add(readSubNode()); break;
                default: throw illegalState();
            }
        }

        return result;
    }

    private ChildSubNode readSubNode() throws XMLStreamException {
        String attr1 = null;
        String value;

        while (nextAttribute()) {
            switch (getAttributeLocalName()) {
                case "attr1":
                    attr1 = getAttributeString();
                    break;
                default:
                    throw illegalState();
            }
        }

        value = getText();

        endElement();

        return new ChildSubNode(attr1, value);
    }

    private ChildNodeB readChildNodeB() throws XMLStreamException {
        ChildNodeBType type = null;
        ChildSubNode subNode = null;

        while (nextAttribute()) {
            switch (getAttributeLocalName()) {
                case "type": type = getAttributeEnum(ChildNodeBType.class); break;
                default: throw illegalState();
            }
        }

        while (nextChild()) {
            switch (getLocalName()) {
                case "subNode": subNode = readSubNode(); break;
                default: throw illegalState();
            }
        }

        return new ChildNodeB(type, subNode);
    }
}
