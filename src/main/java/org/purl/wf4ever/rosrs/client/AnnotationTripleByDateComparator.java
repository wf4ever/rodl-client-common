package org.purl.wf4ever.rosrs.client;

import java.util.Comparator;

/**
 * Compare two quads by the annotation dates. Earlier dates are smaller than later.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTripleByDateComparator implements Comparator<AnnotationTriple> {

    @Override
    public int compare(AnnotationTriple o1, AnnotationTriple o2) {
        return o1.getAnnotation().getCreated().compareTo(o2.getAnnotation().getCreated());
    }

}
