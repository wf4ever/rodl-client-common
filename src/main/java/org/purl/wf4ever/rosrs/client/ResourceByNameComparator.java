package org.purl.wf4ever.rosrs.client;

import java.util.Comparator;

/**
 * Compares two resource by their name.
 * 
 * @author piotrekhol
 * 
 */
final class ResourceByNameComparator implements Comparator<Resource> {

    @Override
    public int compare(Resource o1, Resource o2) {
        return o1.getName() != null ? o1.getName().compareTo(o2.getName()) : -1;
    }
}
