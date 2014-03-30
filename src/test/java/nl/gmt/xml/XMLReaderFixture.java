package nl.gmt.xml;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class XMLReaderFixture {
    @Test
    public void validateValidDocument() throws Exception {
        XMLSchema schema;

        try (InputStream is = getClass().getResourceAsStream("Schema.xsd")) {
            schema = new XMLSchema(is);
        }

        // Run twice to ensure that a single XMLSchema instance can validate multiple documents.

        try (InputStream is = getClass().getResourceAsStream("ValidDocument.xml")) {
            schema.validate(is);
        }

        try (InputStream is = getClass().getResourceAsStream("ValidDocument.xml")) {
            schema.validate(is);
        }
    }

    @Test(expected = XMLSchemaException.class)
    public void validateInvalidDocument() throws Exception {
        XMLSchema schema;

        try (InputStream is = getClass().getResourceAsStream("Schema.xsd")) {
            schema = new XMLSchema(is);
        }

        try (InputStream is = getClass().getResourceAsStream("UnexpectedAttribute.xml")) {
            schema.validate(is);
        }
    }

    @Test
    public void validDocument() throws IOException, XMLStreamException {
        RootNode root = readDocument("ValidDocument.xml");

        assertNotNull(root);
        assertNotNull(root.getNodeA());
        assertNotNull(root.getNodeBs());

        assertEquals("attr1", root.getNodeA().getAttr1());
        assertEquals("attr2", root.getNodeA().getAttr2());

        assertNotNull(root.getNodeA().getSubNodes());
        assertEquals(1, root.getNodeA().getSubNodes().size());

        assertEquals("attr1", root.getNodeA().getSubNodes().get(0).getAttr1());
        assertNull(root.getNodeA().getSubNodes().get(0).getValue());

        assertEquals(2, root.getNodeBs().size());

        assertEquals(ChildNodeBType.A, root.getNodeBs().get(0).getType());
        assertNotNull(root.getNodeBs().get(0).getSubNode());
        assertEquals("attr1", root.getNodeBs().get(0).getSubNode().getAttr1());
        assertEquals("value", root.getNodeBs().get(0).getSubNode().getValue());

        assertEquals(ChildNodeBType.B, root.getNodeBs().get(1).getType());
        assertNotNull(root.getNodeBs().get(1).getSubNode());
        assertEquals("attr1", root.getNodeBs().get(1).getSubNode().getAttr1());
        assertEquals("value", root.getNodeBs().get(1).getSubNode().getValue());
    }

    @Test(expected = XMLStreamException.class)
    public void invalidRootElement() throws IOException, XMLStreamException {
        readDocument("InvalidRootElement.xml");
    }

    @Test
    public void emptyRoot() throws IOException, XMLStreamException {
        readDocument("EmptyRoot.xml");
    }

    @Test
    public void emptyNotSimpleRoot() throws IOException, XMLStreamException {
        readDocument("EmptyNotSimpleRoot.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void invalidChild() throws IOException, XMLStreamException {
        readDocument("InvalidChild.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void invalidAttribute() throws IOException, XMLStreamException {
        readDocument("InvalidAttribute.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void unexpectedAttribute() throws IOException, XMLStreamException {
        readDocument("UnexpectedAttribute.xml");
    }

    private RootNode readDocument(String resourceName) throws IOException, XMLStreamException {
        try (InputStream is = getClass().getResourceAsStream(resourceName)) {
            return new TestReader(is).read();
        }
    }
}
