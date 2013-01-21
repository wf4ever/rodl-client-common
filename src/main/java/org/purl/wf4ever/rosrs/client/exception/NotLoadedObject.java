package org.purl.wf4ever.rosrs.client.exception;

/**
 * This exception indicates the attempt to not loaded object.
 * 
 * @author filip
 * 
 */
public class NotLoadedObject extends RuntimeException {

    /** id. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     * 
     * @param message
     *            the message
     */
    public NotLoadedObject(String message) {
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
    public NotLoadedObject(String message, Exception e) {
        super(message, e);
    }

}
