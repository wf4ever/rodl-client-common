package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;

/**
 * ro:Resource.
 * 
 * @author piotrekhol
 * 
 */
public class Resource {

    /** URI. */
    private URI uri;

    /** The RO it is aggregated by. */
    private ResearchObject researchObject;

    /** URI of the proxy. */
    private URI proxyUri;

    /** ROSRS client. */
    private ROSRService rosrs;

    /** has the resource been loaded from ROSRS. */
    private boolean loaded;


    public Resource(ResearchObject researchObject, URI uri, URI proxyURI) {
        // TODO Auto-generated constructor stub
    }
}
