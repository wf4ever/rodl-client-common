package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;

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
     */
    public AnnotationTriple(Annotation annotation, Annotable subject, URI property, String value) {
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
     */
    public AnnotationTriple(Annotation annotation, Annotable subject, Property property, String value) {
        this.subject = subject;
        this.annotation = annotation;
        this.property = URI.create(property.getURI());
        this.value = value;
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

}
