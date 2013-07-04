package org.purl.wf4ever.rosrs.client;

import java.util.Comparator;

/**
 * Compares two resource by their path.
 * 
 * @author piotrekhol
 * 
 */
public final class ResourceByPathComparator implements Comparator<Resource> {

    @Override
    public int compare(Resource o1, Resource o2) {
        return o1.getPath() != null ? o1.getPath().compareTo(o2.getPath()) : -1;
    }
}
