package org.purl.wf4ever.rosrs.client.exception;

/**
 * Exception that indicates an error when performing a search.
 * 
 * @author piotrekhol
 * 
 */
public class SearchException extends Exception {

    /** id. */
    private static final long serialVersionUID = -2891988826340254599L;


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     */
    public SearchException(String message) {
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
    public SearchException(String message, Exception e) {
        super(message, e);
    }
}
