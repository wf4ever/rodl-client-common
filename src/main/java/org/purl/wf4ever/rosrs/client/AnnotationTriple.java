package org.purl.wf4ever.rosrs.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;
import org.purl.wf4ever.rosrs.client.exception.ROException;
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

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AnnotationTriple.class);

    /** The annotation containing the triple. */
    private final Annotation annotation;

    /** The subject. */
    private final Annotable subject;

    /** The property (predicate). */
    private URI property;

    /** The object. */
    private String value;

    /** True if the values are a merge of multiple values in this annotation. */
    private boolean merge;


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
        this.merge = merge;
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
        this(annotation, subject, property != null ? URI.create(property.getURI()) : null, value, merge);
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


    public void setProperty(URI property) {
        this.property = property;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public boolean isMerge() {
        return merge;
    }


    /**
     * Delete all values of a property describing this resource from an annotation.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public void delete()
            throws ROSRSException {
        annotation.deletePropertyValues(subject, property, merge ? null : value);
        if (annotation.getStatements().isEmpty()) {
            annotation.delete();
        } else {
            annotation.update();
        }
        value = null;
    }


    /**
     * Update the list of statements by setting the property value to a given value. All other values of this property
     * describing this resource are removed.
     * 
     * @param newValue
     *            the new value
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public void updateValue(String newValue)
            throws ROSRSException {
        updatePropertyValue(property, newValue);
    }


    /**
     * Update the list of statements by setting the property and value to given values. All other values of this
     * property describing this resource are removed.
     * 
     * @param newProperty
     *            the new property
     * @param newValue
     *            the new value
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public void updatePropertyValue(URI newProperty, String newValue)
            throws ROSRSException {
        annotation.deletePropertyValues(subject, property, merge ? null : value);
        property = newProperty;
        value = newValue;
        annotation.getStatements().add(new Statement(subject.getUri(), property, value));
        annotation.update();
    }


    /**
     * Return this annotation triple as a {@link Statement}.
     * 
     * @return a new statement
     */
    public Statement asStatement() {
        return new Statement(subject.getUri(), property, value);
    }


    /**
     * Add multiple annotation triples in one HTTP request. The triples to add will all be added in one annotation,
     * annotating the selected resource.
     * 
     * Note that you can pass statements that have other resources as subjects but they will not be returned by this
     * method.
     * 
     * @param annotable
     *            the resource that will be the target of the annotation
     * @param newStatements
     *            statements to add
     * @return triples that have been created that are about the annotable resource
     * @throws ROException
     *             the manifest is incorrect
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public static Set<AnnotationTriple> batchAdd(Annotable annotable, Collection<Statement> newStatements)
            throws ROSRSException, ROException {
        Set<AnnotationTriple> newTriples = new HashSet<>();
        if (!newStatements.isEmpty()) {
            try (InputStream body = Annotation.wrapAnnotationBody(newStatements)) {
                Annotation annotation = annotable.annotate(null, body, RDFFormat.RDFXML.getDefaultMIMEType());
                annotation.load();
                for (Statement statement : annotation.getStatements()) {
                    newTriples.add(new AnnotationTriple(annotation, annotable, statement.getPropertyURI(), statement
                            .getObject(), false));
                }
            } catch (IOException e) {
                LOG.error("Can't close the input stream", e);
            }
        }
        return newTriples;
    }


    /**
     * Remove multiple annotation triples in the minimum number of HTTP requests.
     * 
     * @param triplesToRemove
     *            triples to delete
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public static void batchRemove(Collection<AnnotationTriple> triplesToRemove)
            throws ROSRSException {
        Set<Annotation> modifiedAnnotations = new HashSet<>();
        for (AnnotationTriple triple : triplesToRemove) {
            Annotation annotation = triple.getAnnotation();
            annotation.getStatements().remove(triple.asStatement());
            modifiedAnnotations.add(annotation);
        }
        for (Annotation annotation : modifiedAnnotations) {
            if (annotation.getStatements().isEmpty()) {
                annotation.delete();
            } else {
                annotation.update();
            }
        }
    }


    @Override
    public String toString() {
        return "" + (annotation != null ? annotation.getUri() : "?") + " { " + subject.getUri() + " " + property + " "
                + value + " }";
    }
}
