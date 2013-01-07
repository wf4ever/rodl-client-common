package org.purl.wf4ever.rosrs.client;

import java.util.Collection;

/**
 * An interface for the resource that may be the target of an ro:AggregatedAnnotation.
 * 
 * @author piotrekhol
 * 
 */
public interface Annotable {

    /**
     * Get all annotations with the target including this resource.
     * 
     * @return a collection of annotations, may be immutable
     */
    Collection<Annotation> getAnnotations();

}
