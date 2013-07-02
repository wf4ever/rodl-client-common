package org.purl.wf4ever.rosrs.client;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * An interface for the resource that may be the target of an ro:AggregatedAnnotation.
 * 
 * @author piotrekhol
 * 
 */
public interface Annotable extends Displayable, Serializable {

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
     * Find all properties of this resource in all annotations.
     * 
     * @param property
     *            the URI of the property
     * @param merge
     *            If an annotation has many values for this property, merge them using a semicolon "; ".
     * @return a map of annotation and property values found in their bodies
     */
    List<AnnotationTriple> getPropertyValues(URI property, boolean merge);


    /**
     * Find all properties of this resource in all annotations.
     * 
     * @param property
     *            the Jena the property
     * @param merge
     *            If an annotation has many values for this property, merge them using a semicolon "; ".
     * @return a map of annotation and property values found in their bodies
     */
    List<AnnotationTriple> getPropertyValues(Property property, boolean merge);


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
    AnnotationTriple createPropertyValue(URI property, String value)
            throws ROSRSException, ROException;


    /**
     * Create a new annotation describing this resource with a given property and URI value.
     * 
     * @param property
     *            the URI of the property
     * @param value
     *            the value to be used as an RDF node
     * @return the new annotation
     * @throws ROSRSException
     *             server returned an unexpected response
     * @throws ROException
     *             the manifest is incorrect
     */
    AnnotationTriple createPropertyValue(URI property, URI value)
            throws ROSRSException, ROException;


    /**
     * Create a new annotation describing this resource with a given property and literal value.
     * 
     * @param property
     *            the property
     * @param value
     *            the value to be used as a literal
     * @return the new annotation
     * @throws ROSRSException
     *             server returned an unexpected response
     * @throws ROException
     *             the manifest is incorrect
     */
    AnnotationTriple createPropertyValue(Property property, String value)
            throws ROSRSException, ROException;


    /**
     * Create a new annotation describing this resource with a given property and URI value.
     * 
     * @param property
     *            the property
     * @param value
     *            the value to be used as an RDF node
     * @return the new annotation
     * @throws ROSRSException
     *             server returned an unexpected response
     * @throws ROException
     *             the manifest is incorrect
     */
    AnnotationTriple createPropertyValue(Property property, URI value)
            throws ROSRSException, ROException;


    /**
     * Return a list of all quads for which the subject is this resource, sorted by the predicate local name.
     * 
     * @return a list of annotation triples, possibly empty
     */
    List<AnnotationTriple> getAnnotationTriples();


    /**
     * Returns true if the metadata of this resource have been loaded. The client should not try to load any annotation
     * if this method returns false.
     * 
     * @return true if the metadata have been loaded, false otherwise.
     */
    boolean isLoaded();
}
