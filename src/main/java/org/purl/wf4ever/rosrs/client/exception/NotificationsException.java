package org.purl.wf4ever.rosrs.client.exception;

/**
 * Exception that indicates an error when retrieving notifications.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationsException extends Exception {

    /** id. */
    private static final long serialVersionUID = -2891988826340254599L;


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     */
    public NotificationsException(String message) {
        super(message);
    }


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     * @param e
     *            cause
     */
    public NotificationsException(String message, Exception e) {
        super(message, e);
    }
}
