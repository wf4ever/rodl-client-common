package org.purl.wf4ever.rosrs.client.exception;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Exception that indicates an unexpected response from the ROSR Service.
 * 
 * @author piotrekhol
 * 
 */
public class ROSRSException extends Exception {

    /** id. */
    private static final long serialVersionUID = -2891988826340254599L;

    /** response status. */
    private final int status;

    /** response reason. */
    private final String reason;


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     * @param status
     *            Response status
     * @param reason
     *            Response reason
     * @param details
     *            details of the error
     */
    public ROSRSException(String message, int status, String reason, String details) {
        super(String.format("%s (%d %s): %s", message, status, reason, details != null ? details : "-"));
        this.status = status;
        this.reason = reason;
    }


    /**
     * Constructor.
     * 
     * @param message
     *            Context message
     * @param response
     *            from the server
     */
    public ROSRSException(String message, ClientResponse response) {
        this(message, response.getStatus(), response.getClientResponseStatus().getReasonPhrase(), response
                .getEntity(String.class));
    }


    public int getStatus() {
        return status;
    }


    public String getReason() {
        return reason;
    }
}
