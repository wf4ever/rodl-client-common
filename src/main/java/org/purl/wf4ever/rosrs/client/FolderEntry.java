package org.purl.wf4ever.rosrs.client;

import java.net.URI;

/**
 * ro:FolderEntry.
 * 
 * @author piotrekhol
 * 
 */
public class FolderEntry {

    /** aggregating folder. */
    private Folder folder;

    /** name in the folder. */
    private String name;

    /** folder entry URI. */
    private URI uri;

    /** URI of the resource it points to. */
    private URI resource;


    /**
     * Constructor.
     * 
     * @param folder
     *            aggregating folder
     * @param uri
     *            folder entry URI
     * @param resource
     *            URI of the resource it points to
     * @param entryName
     *            name in the folder
     */
    public FolderEntry(Folder folder, URI uri, URI resource, String entryName) {
        this.folder = folder;
        this.uri = uri;
        this.resource = resource;
        this.name = entryName;
    }


    public Folder getFolder() {
        return folder;
    }


    public String getName() {
        return name;
    }


    public URI getUri() {
        return uri;
    }


    public URI getResource() {
        return resource;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
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
        FolderEntry other = (FolderEntry) obj;
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        } else if (!resource.equals(other.resource)) {
            return false;
        }
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
