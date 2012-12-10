package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;

/**
 * ro:FolderEntry.
 * 
 * @author piotrekhol
 * 
 */
public class FolderEntry {

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

}
