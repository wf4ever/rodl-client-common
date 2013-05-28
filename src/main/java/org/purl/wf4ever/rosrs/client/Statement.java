/**
 * 
 */
package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * A simplified verion of {@link com.hp.hpl.jena.rdf.model.Statement}, which unlike the original is serializable.
 * 
 * @author piotrhol
 * 
 */
public class Statement implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1704407898614509230L;

    /** Is the subject a resource with a URI, based on {@link RDFNode#isURIResource()}. */
    private final boolean isSubjectURIResource;

    /** The subject URI if it's a resource with URI, the String value otherwise. */
    private final String subjectValue;

    /** The subject URI if it's a resource with URI, null otherwise. */
    private final URI subjectURI;

    /** Property URI. */
    private URI propertyURI;

    /** Property local name, based on {@link Property#getLocalName()}. */
    private String propertyLocalName;

    /** Annotation that this statement belongs to. */
    private Annotation annotation;

    /** The object URI if it's a resource with URI, the String value otherwise. */
    private String objectValue;

    /** The object URI if it's a resource with URI, the String value otherwise. */
    private URI objectURI;


    /**
     * Constructor.
     * 
     * @param original
     *            the original {@link com.hp.hpl.jena.rdf.model.Statement}
     * @param annotation
     *            annotation this statement belongs to
     * @throws URISyntaxException
     *             some URI in the original is incorrect
     */
    public Statement(com.hp.hpl.jena.rdf.model.Statement original, Annotation annotation)
            throws URISyntaxException {
        isSubjectURIResource = original.getSubject().isURIResource();
        if (isSubjectURIResource) {
            subjectURI = new URI(original.getSubject().getURI());
            subjectValue = original.getSubject().getURI();
        } else {
            subjectURI = null;
            subjectValue = original.getSubject().asResource().getId().getLabelString();
        }
        setPropertyURI(new URI(original.getPredicate().getURI()));
        RDFNode node = original.getObject();
        if (node.isURIResource()) {
            objectURI = new URI(node.asResource().getURI());
            objectValue = node.asResource().toString();
        } else if (node.isResource()) {
            objectURI = null;
            objectValue = original.getObject().asResource().getId().getLabelString();
        } else {
            objectURI = null;
            objectValue = original.getObject().asLiteral().getValue().toString();
        }
        this.annotation = annotation;
    }


    /**
     * Constructor for an empty statement. The default property is dcterms:description, the default value is "".
     * 
     * @param subjectURI
     *            subject URI
     * @param annotation
     *            annotation this statement belongs to
     */
    //FIXME this constructor should be deleted
    public Statement(URI subjectURI, Annotation annotation) {
        this(subjectURI, URI.create(DCTerms.description.getURI()), "");
        this.annotation = annotation;
    }


    /**
     * Constructor.
     * 
     * @param subject
     *            subject
     * @param property
     *            property
     * @param value
     *            a literal value
     */
    public Statement(URI subject, URI property, String value) {
        this.subjectURI = subject;
        subjectValue = "";
        isSubjectURIResource = false;
        setPropertyURI(property);
        objectURI = null;
        objectValue = value;
    }


    public Annotation getAnnotation() {
        return annotation;
    }


    public URI getPropertyURI() {
        return propertyURI;
    }


    public String getPropertyLocalName() {
        return propertyLocalName;
    }


    public String getPropertyLocalNameNice() {
        return Utils.splitCamelCase(getPropertyLocalName()).toLowerCase();
    }


    public String getObjectValue() {
        return objectValue;
    }


    public URI getObjectURI() {
        return objectURI;
    }


    public boolean isObjectURIResource() {
        return this.objectURI != null;
    }


    /**
     * Set property URI and its local name.
     * 
     * @param propertyURI
     *            property URI, not null
     */
    public void setPropertyURI(URI propertyURI) {
        if (propertyURI == null) {
            throw new NullPointerException("Property URI cannot be null");
        }
        this.propertyURI = propertyURI;
        this.propertyLocalName = ModelFactory.createDefaultModel().createProperty(propertyURI.toString())
                .getLocalName();
    }


    /**
     * Is object a resource with a URI.
     * 
     * @param isObjectURIResource
     *            is it
     */
    public void setObjectURIResource(boolean isObjectURIResource) {
        if (isObjectURIResource) {
            this.objectURI = URI.create("");
            this.objectValue = null;
        } else {
            this.objectURI = null;
            this.objectValue = "";
        }
    }


    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }


    /**
     * Set object URI, resolved against subject URI.
     * 
     * @param objectURI
     *            object URI
     */
    public void setObjectURI(URI objectURI) {
        this.objectURI = subjectURI.resolve(objectURI);
    }


    public URI getSubjectURI() {
        return subjectURI;
    }


    public String getSubjectValue() {
        return subjectValue;
    }


    public boolean isSubjectURIResource() {
        return isSubjectURIResource;
    }


    /**
     * Create a {@link com.hp.hpl.jena.rdf.model.Statement} based on this one.
     * 
     * @return a statement
     */
    public com.hp.hpl.jena.rdf.model.Statement createJenaStatement() {
        Model model = ModelFactory.createDefaultModel();
        Resource subject = model.createResource(subjectURI.toString());
        Property property = model.createProperty(propertyURI.toString());
        RDFNode object = null;
        if (isObjectURIResource()) {
            object = model.createResource(objectURI.toString());
        } else {
            object = model.createTypedLiteral(objectValue);
        }
        return model.createStatement(subject, property, object);
    }


    /**
     * Return if the object of this statement is a literal.
     * 
     * @return true if it's a literal
     */
    public boolean isObjectLiteral() {
        //FIXME this will return true also for blank nodes
        return !isObjectURIResource();
    }


    @Override
    public String toString() {
        return subjectValue + " " + propertyURI + " " + objectValue;
    }

}
