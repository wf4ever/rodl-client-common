package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;

import org.joda.time.DateTime;

/**
 * ro:Resource.
 * 
 * @author piotrekhol
 * 
 */
public class Resource {

    /** URI. */
    protected final URI uri;

    /** The RO it is aggregated by. */
    protected final ResearchObject researchObject;

    /** URI of the proxy. */
    protected final URI proxyUri;

    /** ROSRS client. */
    protected ROSRService rosrs;

    /** creator URI. */
    protected URI creator;

    /** creation date. */
    protected DateTime created;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            The RO it is aggregated by
     * @param uri
     *            resource URI
     * @param proxyURI
     *            URI of the proxy
     * @param creator
     *            author of the resource
     * @param created
     *            creation date
     */
    public Resource(ResearchObject researchObject, URI uri, URI proxyURI, URI creator, DateTime created) {
        this.researchObject = researchObject;
        this.uri = uri;
        this.proxyUri = proxyURI;
        this.creator = creator;
        this.created = created;
    }


    public URI getUri() {
        return uri;
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public URI getProxyUri() {
        return proxyUri;
    }


    public ROSRService getRosrs() {
        return rosrs;
    }


    public URI getCreator() {
        return creator;
    }


    public DateTime getCreated() {
        return created;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((proxyUri == null) ? 0 : proxyUri.hashCode());
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
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
        if (proxyUri == null) {
            if (other.proxyUri != null) {
                return false;
            }
        } else if (!proxyUri.equals(other.proxyUri)) {
            return false;
        }
        return true;
    }

}
