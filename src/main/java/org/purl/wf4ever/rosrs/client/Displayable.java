package org.purl.wf4ever.rosrs.client;

import java.net.URI;

/**
 * A Thing that can be displayed.
 * 
 * @author piotrekhol
 * 
 */
public interface Displayable {

    /**
     * Resource URI.
     * 
     * @return the URI
     */
    URI getUri();


    /**
     * Resource human friendly name.
     * 
     * @return name
     */
    String getName();

}
