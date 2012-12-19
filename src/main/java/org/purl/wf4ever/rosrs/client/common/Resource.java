package org.purl.wf4ever.rosrs.client.common;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;

import org.joda.time.DateTime;

import com.google.common.collect.Multimap;
import com.sun.jersey.api.client.ClientResponse;

/**
 * ro:Resource.
 * 
 * @author piotrekhol
 * 
 */
public class Resource implements Serializable {

    /** id. */
    private static final long serialVersionUID = 7593887876508190085L;

    /** URI. */
    protected final URI uri;

    /** The RO it is aggregated by. */
    protected final ResearchObject researchObject;

    /** URI of the proxy. */
    protected final URI proxyUri;

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


    /**
     * Add an internal resource to the research object. Does not add the resource instance to the {@link ResearchObject}
     * instance.
     * 
     * @param researchObject
     *            the RO
     * @param path
     *            resource path, relative to the RO URI
     * @param content
     *            resource content
     * @param contentType
     *            resource Content Type
     * @return the resource instance
     * @throws ROSRSException
     *             server returned an unexpected response
     */
    public static Resource create(ResearchObject researchObject, String path, InputStream content, String contentType)
            throws ROSRSException {
        ClientResponse response = researchObject.getRosrs().aggregateInternalResource(researchObject.getUri(), path,
            content, contentType);
        Multimap<String, URI> headers = Utils.getLinkHeaders(response.getHeaders().get("Link"));
        URI resource = headers.get("http://www.openarchives.org/ore/terms/proxyFor").isEmpty() ? null : headers
                .get("http://www.openarchives.org/ore/terms/proxyFor").iterator().next();
        response.close();
        //FIXME creator/created dates are null but see WFE-758
        return new Resource(researchObject, resource, response.getLocation(), null, null);
    }


    /**
     * Add an external resource (a reference to a resource) to the research object. Does not add the resource instance
     * to the {@link ResearchObject} instance.
     * 
     * @param researchObject
     *            the RO
     * @param uri
     *            resource URI
     * @return the resource instance
     * @throws ROSRSException
     *             server returned an unexpected response
     */
    public static Resource create(ResearchObject researchObject, URI uri)
            throws ROSRSException {
        ClientResponse response = researchObject.getRosrs().aggregateExternalResource(researchObject.getUri(), uri);
        Multimap<String, URI> headers = Utils.getLinkHeaders(response.getHeaders().get("Link"));
        URI proxy = headers.get("http://www.openarchives.org/ore/terms/proxyFor").isEmpty() ? null : headers
                .get("http://www.openarchives.org/ore/terms/proxyFor").iterator().next();
        response.close();
        //FIXME creator/created dates are null but see WFE-758
        return new Resource(researchObject, response.getLocation(), proxy, null, null);
    }


    /**
     * Delete the resource from ROSRS and from the research object.
     * 
     * @throws ROSRSException
     *             server returned an unexpected response
     */
    public void delete()
            throws ROSRSException {
        researchObject.getRosrs().deleteResource(uri);
        researchObject.removeResource(this);
    }


    public Collection<Annotation> getAnnotations() {
        return this.researchObject.getAnnotations().get(uri);
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