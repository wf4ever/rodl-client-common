package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;

import org.joda.time.DateTime;

/**
 * ro:Folder.
 * 
 * @author piotrekhol
 * 
 */
public class Folder extends Resource {

    /** id. */
    private static final long serialVersionUID = 8014407879648172595L;

    /** Resource map (graph with folder description) URI. */
    private URI resourceMap;

    /** has the resource map been loaded. */
    private boolean loaded;

    /** is the folder a root folder in the RO. */
    private boolean rootFolder;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            The RO it is aggregated by
     * @param uri
     *            resource URI
     * @param proxyURI
     *            URI of the proxy
     * @param resourceMap
     *            Resource map (graph with folder description) URI
     * @param creator
     *            author of the resource
     * @param created
     *            creation date
     * @param rootFolder
     *            is the folder a root folder in the RO
     */
    public Folder(ResearchObject researchObject, URI uri, URI proxyURI, URI resourceMap, URI creator, DateTime created,
            boolean rootFolder) {
        super(researchObject, uri, proxyURI, creator, created);
        this.resourceMap = resourceMap;
        this.rootFolder = rootFolder;
        this.loaded = false;
    }


    public URI getResourceMap() {
        return resourceMap;
    }


    public boolean isLoaded() {
        return loaded;
    }


    public boolean isRootFolder() {
        return rootFolder;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((proxyUri == null) ? 0 : proxyUri.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + ((resourceMap == null) ? 0 : resourceMap.hashCode());
        result = prime * result + (rootFolder ? 1231 : 1237);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Folder other = (Folder) obj;
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
        if (resourceMap == null) {
            if (other.resourceMap != null) {
                return false;
            }
        } else if (!resourceMap.equals(other.resourceMap)) {
            return false;
        }
        if (rootFolder != other.rootFolder) {
            return false;
        }
        return true;
    }

}
