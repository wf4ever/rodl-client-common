package org.purl.wf4ever.rosrs.client;

import java.io.InputStream;
import java.util.Collection;

import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * An interface for the resource that may be the target of an ro:AggregatedAnnotation.
 * 
 * @author piotrekhol
 * 
 */
public interface Annotable extends Displayable {

    /**
     * Get all annotations with the target including this resource.
     * 
     * @return a collection of annotations, may be immutable
     */
    Collection<Annotation> getAnnotations();


    /**
     * Create a new annotation and an annotation body about this resource.
     * 
     * @param bodyPath
     *            suggested path of the annotation body, relative to the RO URI, may be null
     * @param body
     *            RDF graph input stream
     * @param bodyContentType
     *            RDF graph content type
     * @return the annotation instance
     * @throws ROException
     * @throws ROSRSException
     */
    Annotation annotate(String bodyPath, InputStream body, String bodyContentType)
            throws ROSRSException, ROException;

}
