package nl.gmt.xml;

import org.apache.commons.lang.Validate;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class XMLSchema {
    private final Schema schema;

    public XMLSchema(Reader reader) throws XMLSchemaException {
        this(createXMLStreamReader(reader));
    }

    public XMLSchema(InputStream is) throws XMLSchemaException {
        this(createXMLStreamReader(is));
    }

    private XMLSchema(XMLStreamReader reader) throws XMLSchemaException {
        try {
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StAXSource(reader));
        } catch (SAXException e) {
            throw new XMLSchemaException(e);
        }
    }

    private static XMLStreamReader createXMLStreamReader(Reader reader) throws XMLSchemaException {
        Validate.notNull(reader, "reader");

        try {
            return XMLInputFactory.newInstance().createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new XMLSchemaException(e);
        }
    }

    private static XMLStreamReader createXMLStreamReader(InputStream is) throws XMLSchemaException {
        Validate.notNull(is, "is");

        try {
            return XMLInputFactory.newInstance().createXMLStreamReader(is);
        } catch (XMLStreamException e) {
            throw new XMLSchemaException(e);
        }
    }

    public void validate(Reader reader) throws XMLSchemaException {
        validate(createXMLStreamReader(reader));
    }

    public void validate(InputStream is) throws XMLSchemaException {
        validate(createXMLStreamReader(is));
    }

    private void validate(XMLStreamReader reader) throws XMLSchemaException {
        Validator validator = schema.newValidator();

        try {
            validator.validate(new StAXSource(reader));
        } catch (SAXException|IOException e) {
            throw new XMLSchemaException(e);
        }
    }
}
