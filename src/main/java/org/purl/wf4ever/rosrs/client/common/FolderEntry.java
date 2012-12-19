package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;

/**
 * ro:FolderEntry.
 * 
 * @author piotrekhol
 * 
 */
public class FolderEntry {

    public FolderEntry(Folder folder, URI eURI, URI rURI, String name2) {
        // TODO Auto-generated constructor stub
    }


    /** folder. */
    private Folder parentFolder;

    /** name in the folder. */
    private String name;

    /** folder entry URI. */
    private URI uri;

    /** URI of the resource it points to. */
    private URI proxyFor;

    /** ROSRS client. */
    private ROSRService rosrs;


    public URI getResource() {
        // TODO Auto-generated method stub
        return null;
    }

}
