package org.purl.wf4ever.rosrs.client.exception;

/**
 * This exception indicates the attempt to get a property that was not loaded.
 * 
 * @author filip
 * 
 */
public class ObjectNotLoadedException extends RuntimeException {

    /** id. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     * 
     * @param message
     *            the message
     */
    public ObjectNotLoadedException(String message) {
        super(message);
    }


    /**
     * Constructor.
     * 
     * @param message
     *            the message
     * @param e
     *            the original exception
     */
    public ObjectNotLoadedException(String message, Exception e) {
        super(message, e);
    }

}
