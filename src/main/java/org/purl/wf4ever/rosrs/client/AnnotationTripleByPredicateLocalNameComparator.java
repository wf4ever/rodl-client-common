package org.purl.wf4ever.rosrs.client;

import java.util.Comparator;

/**
 * Compare two quads by the local name of the predicate.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTripleByPredicateLocalNameComparator implements Comparator<AnnotationTriple> {

    @Override
    public int compare(AnnotationTriple o1, AnnotationTriple o2) {
        String path1 = o1.getProperty().getPath();
        String name1 = path1.contains("/") ? path1.substring(path1.lastIndexOf("/")) : path1;
        String path2 = o2.getProperty().getPath();
        String name2 = path2.contains("/") ? path2.substring(path2.lastIndexOf("/")) : path2;
        return name1.compareToIgnoreCase(name2);
    }

}
