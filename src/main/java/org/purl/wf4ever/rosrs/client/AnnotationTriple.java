package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;

import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Represents a triple in an annotation (a quad), for example <x> rfds:comment <y> in annotation <z>.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTriple implements Serializable {

    /** id. */
    private static final long serialVersionUID = 889959786166231224L;

    /** The annotation containing the triple. */
    private final Annotation annotation;

    /** The subject. */
    private final Annotable subject;

    /** The property (predicate). */
    private final URI property;

    /** The object. */
    private final String value;


    /**
     * Constructor.
     * 
     * @param annotation
     *            the annotation containing the triple
     * @param subject
     *            the subject
     * @param property
     *            the property (predicate)
     * @param value
     *            the object
     * @param merge
     *            true if the values are a merge of multiple values in this annotation
     * @param anyExisting
     */
    public AnnotationTriple(Annotation annotation, Annotable subject, URI property, String value, boolean merge) {
        this.subject = subject;
        this.annotation = annotation;
        this.property = property;
        this.value = value;
    }


    /**
     * Constructor.
     * 
     * @param annotation
     *            the annotation containing the triple
     * @param subject
     *            the subject
     * @param property
     *            the property (predicate) as a Jena object
     * @param value
     *            the object
     * @param merge
     *            true if the values are a merge of multiple values in this annotation
     */
    public AnnotationTriple(Annotation annotation, Annotable subject, Property property, String value, boolean merge) {
        this(annotation, subject, URI.create(property.getURI()), value, merge);
    }


    public Annotation getAnnotation() {
        return annotation;
    }


    public Annotable getSubject() {
        return subject;
    }


    public URI getProperty() {
        return property;
    }


    public String getValue() {
        return value;
    }


    /**
     * Delete all literal values of a property describing this resource from an annotation. Property values that are not
     * literals are ignored (preserved).
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public void delete()
            throws ROSRSException {
        annotation.deletePropertyValue(subject, property);
        if (annotation.getStatements().isEmpty()) {
            annotation.delete();
        } else {
            annotation.update();
        }
    }


    /**
     * Update an annotation by setting the property value to a given literal value. All other literal values of this
     * property describing this resource are removed.
     * 
     * @param object
     *            the value to be used as a literal
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public void setValue(String object)
            throws ROSRSException {
        annotation.updatePropertyValue(subject, property, object);
        annotation.update();
    }

}
