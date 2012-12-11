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


    /**
     * Constructor.
     * 
     * @param researchObject
     *            The RO it is aggregated by
     * @param uri
     *            resource URI
     * @param proxyURI
     *            URI of the proxy
     */
    public Resource(ResearchObject researchObject, URI uri, URI proxyURI) {
        this.researchObject = researchObject;
        this.uri = uri;
        this.proxyUri = proxyURI;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Resource other = (Resource) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

}
