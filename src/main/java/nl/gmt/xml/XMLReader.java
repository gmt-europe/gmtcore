package nl.gmt.xml;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class XMLReader<T> {
    private final XMLStreamReader reader;
    private int attribute = -1;
    private int nodeType = -1;
    private boolean readAttributes = false;
    private final Map<Class<? extends AttributeEnum>, EnumCache<? extends AttributeEnum>> enumCache = new HashMap<>();

    public XMLReader(Reader reader) throws XMLStreamException {
        Validate.notNull(reader, "reader");

        this.reader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
    }

    public XMLReader(InputStream is) throws XMLStreamException {
        Validate.notNull(is, "is");

        reader = XMLInputFactory.newInstance().createXMLStreamReader(is);
    }

    public abstract T read() throws XMLStreamException;

    protected XMLStreamException illegalState() {
        return new XMLStreamException("Cannot parse XML document");
    }

    private int next() throws XMLStreamException {
        if (nodeType != -1) {
            int result = nodeType;
            nodeType = -1;
            return result;
        }

        return reader.next();
    }

    private int peekNode() throws XMLStreamException {
        if (nodeType != -1) {
            return nodeType;
        }

        nodeType = reader.next();
        return nodeType;
    }

    private boolean hasNext() throws XMLStreamException {
        return nodeType != -1 || reader.hasNext();
    }

    protected void endElement() throws XMLStreamException {
        if (nextChild()) {
            throw new XMLStreamException("Did not expect a child element");
        }
    }

    protected boolean nextChild() throws XMLStreamException {
        if (
            reader.getEventType() == XMLStreamReader.START_ELEMENT &&
            !readAttributes &&
            reader.getAttributeCount() > 0
        ) {
            throw new XMLStreamException("Did not expect any attributes");
        }

        readAttributes = false;

        while (hasNext()) {
            switch (next()) {
                case XMLStreamReader.START_ELEMENT:
                    return true;
                case XMLStreamReader.END_ELEMENT:
                    return false;
                case XMLStreamReader.COMMENT:
                    break;

                case XMLStreamReader.CHARACTERS:
                    if (!StringUtils.isBlank(reader.getText())) {
                        throw new XMLStreamException("Did not expect content");
                    }
                    break;
            }
        }

        throw new XMLStreamException(String.format("Did not expect element of type '%d'", reader.getEventType()));
    }

    protected void nextElement(String name) throws XMLStreamException {
        Validate.notNull(name, "name");

        if (!nextChild()) {
            throw new XMLStreamException("Expected a child element");
        }

        if (!name.equals(getLocalName())) {
            throw new XMLStreamException(String.format("Expected element '%s'", name));
        }
    }

    protected void nextElement(QName name) throws XMLStreamException {
        Validate.notNull(name, "name");

        if (!nextChild()) {
            throw new XMLStreamException("Expected a child element");
        }

        if (!name.equals(getName())) {
            throw new XMLStreamException(String.format("Expected element '%s'", name));
        }
    }

    protected boolean nextAttribute() {
        readAttributes = true;

        attribute++;

        if (attribute >= reader.getAttributeCount()) {
            attribute = -1;
            return false;
        }

        return true;
    }

    protected String getAttributeLocalName() throws XMLStreamException {
        return getAttributeName().getLocalPart();
    }

    protected QName getAttributeName() throws XMLStreamException {
        if (attribute == -1) {
            throw new XMLStreamException("No attribute available");
        }

        return reader.getAttributeName(attribute);
    }

    protected String getText() throws XMLStreamException {
        while (hasNext()) {
            switch (peekNode()) {
                case XMLStreamReader.COMMENT:
                    next();
                    break;

                case XMLStreamReader.CHARACTERS:
                    next();
                    return reader.getText();

                default:
                    return null;
            }
        }

        return null;
    }

    protected String getAttributeString() throws XMLStreamException {
        if (attribute == -1) {
            throw new XMLStreamException("No attribute available");
        }

        return reader.getAttributeValue(attribute);
    }

    protected <T extends AttributeEnum> T getAttributeEnum(Class<T> enumClass) throws XMLStreamException {
        return convertToEnum(getAttributeString(), enumClass);
    }

    protected int getAttributeInt() throws XMLStreamException {
        return DatatypeConverter.parseInt(getAttributeString());
    }

    protected boolean getAttributeBoolean() throws XMLStreamException {
        return DatatypeConverter.parseBoolean(getAttributeString());
    }

    @SuppressWarnings("unchecked")
    private <T extends AttributeEnum> T convertToEnum(String value, Class<T> enumClass) throws XMLStreamException {
        Validate.notNull(enumClass, "enumClass");

        if (value == null) {
            return null;
        }

        EnumCache<T> cache = (EnumCache<T>)enumCache.get(enumClass);

        if (cache == null) {
            cache = new EnumCache<>(enumClass);
            enumCache.put(enumClass,  cache);
        }

        return cache.getItem(value);
    }

    protected QName getName() {
        return reader.getName();
    }

    protected String getLocalName() {
        return getName().getLocalPart();
    }

    private static class EnumCache<T extends AttributeEnum> {
        private final Map<String, T> values = new HashMap<>();
        private final Class<T> enumClass;

        private EnumCache(Class<T> enumClass) {
            this.enumClass = enumClass;
            for (T item : enumClass.getEnumConstants()) {
                values.put(item.getValue(), item);
            }
        }

        public T getItem(String value) throws XMLStreamException {
            T result = values.get(value);

            if (result == null) {
                throw new XMLStreamException(String.format("Cannot parse '%s' as '%s'", value, enumClass));
            }

            return result;
        }
    }
}
