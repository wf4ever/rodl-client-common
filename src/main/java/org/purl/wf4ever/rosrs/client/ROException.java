package org.purl.wf4ever.rosrs.client;

import java.net.URI;

/**
 * Exception that indicates incorrect RO metadata from RODL.
 * 
 * @author piotrekhol
 * 
 */
public class ROException extends Exception {

    /** id. */
    private static final long serialVersionUID = -2891988826340254599L;


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     */
    public ROException(String message) {
        super(message);
    }


    /**
     * Constructor.
     * 
     * @param message
     *            message
     * @param uri
     *            RO URI
     */
    public ROException(String message, URI uri) {
        super(String.format("%s (%s)", message, uri.toString()));
    }

}
