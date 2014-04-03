package nl.gmt.xml;

public class XMLSchemaException extends Exception {
    public XMLSchemaException() {
    }

    public XMLSchemaException(String message) {
        super(message);
    }

    public XMLSchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLSchemaException(Throwable cause) {
        super(cause);
    }

    public XMLSchemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
