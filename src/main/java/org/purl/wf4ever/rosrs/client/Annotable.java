package org.purl.wf4ever.rosrs.client;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

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
     * @throws ROSRSException
     *             server returned an unexpected response
     * @throws ROException
     *             the manifest is incorrect
     */
    Annotation annotate(String bodyPath, InputStream body, String bodyContentType)
            throws ROSRSException, ROException;


    /**
     * Find all literal properties of this resource in all annotations. If an annotation has many values for this
     * property, merge them using a semicolon "; ". Properties whose values are not literals are ignored.
     * 
     * @param property
     *            the URI of the property
     * @return a map of annotation and property values found in their bodies
     */
    Map<Annotation, String> getPropertyValues(URI property);


    /**
     * Create a new annotation describing this resource with a given property and literal value.
     * 
     * @param property
     *            the URI of the property
     * @param value
     *            the value to be used as a literal
     * @return the new annotation
     * @throws ROSRSException
     *             server returned an unexpected response
     * @throws ROException
     *             the manifest is incorrect
     */
    Annotation createPropertyValue(URI property, String value)
            throws ROSRSException, ROException;


    /**
     * Update an annotation by setting the property value to a given literal value. All other literal values of this
     * property describing this resource are removed.
     * 
     * @param annotation
     *            the annotation in which the new value should be stored
     * @param property
     *            the URI of the property
     * @param value
     *            the value to be used as a literal
     * @return the updated annotation
     * @throws ROSRSException
     *             unexpected response from the server
     */
    Annotation updatePropertyValue(Annotation annotation, URI property, String value)
            throws ROSRSException;


    /**
     * Delete all literal values of a property describing this resource from an annotation. Property values that are not
     * literals are ignored (preserved).
     * 
     * @param annotation
     *            the annotation from which to delete the property value
     * @param property
     *            the URI of the property
     * @throws ROSRSException
     *             unexpected response from the server
     */
    void deletePropertyValue(Annotation annotation, URI property)
            throws ROSRSException;


    /**
     * Returns true if the metadata of this resource have been loaded. The client should not try to load any annotation
     * if this method returns false.
     * 
     * @return true if the metadata have been loaded, false otherwise.
     */
    boolean isLoaded();
}
