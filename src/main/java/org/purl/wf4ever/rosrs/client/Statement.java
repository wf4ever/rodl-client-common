/**
 * 
 */
package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A simplified verion of {@link com.hp.hpl.jena.rdf.model.Statement}, which unlike the original is serializable.
 * 
 * @author piotrhol
 * 
 */
public class Statement implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1704407898614509230L;

    /** The subject URI if it's a resource with URI, the String value otherwise. */
    private final String subjectValue;

    /** The subject URI if it's a resource with URI, null otherwise. */
    private final URI subjectURI;

    /** Property URI. */
    private final URI propertyURI;

    /** The object as String. If the original object is a URI reference, the URI is used. */
    private final String object;


    /**
     * Create a statement from a Jena statement.
     * 
     * @param original
     *            the original {@link com.hp.hpl.jena.rdf.model.Statement}
     * @return a new statement
     */
    public static Statement create(com.hp.hpl.jena.rdf.model.Statement original) {
        URI property = URI.create(original.getPredicate().getURI());
        RDFNode node = original.getObject();
        String object;
        if (node.isURIResource()) {
            object = node.asResource().getURI();
        } else if (node.isResource()) {
            object = original.getObject().asResource().getId().getLabelString();
        } else {
            object = original.getObject().asLiteral().getValue().toString();
        }
        Statement statement;
        if (original.getSubject().isURIResource()) {
            URI subject = URI.create(original.getSubject().getURI());
            statement = new Statement(subject, property, object);
        } else {
            String subject = original.getSubject().asResource().getId().getLabelString();
            statement = new Statement(subject, property, object);
        }
        return statement;
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
        Objects.requireNonNull(property);
        this.subjectURI = subject;
        this.subjectValue = subject.toString();
        this.propertyURI = property;
        object = value;
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
    public Statement(String subject, URI property, String value) {
        Objects.requireNonNull(property);
        this.subjectURI = null;
        this.subjectValue = subject;
        this.propertyURI = property;
        object = value;
    }


    public URI getPropertyURI() {
        return propertyURI;
    }


    public String getObject() {
        return object;
    }


    public URI getSubjectURI() {
        return subjectURI;
    }


    public String getSubjectValue() {
        return subjectValue;
    }


    /**
     * Check if the statement has the subject, predicate or object matching the given parameters. Any parameter can be
     * set to null to match everything.
     * 
     * @param subjectUri2
     *            subject to match or null
     * @param property2
     *            property to match or null
     * @param value2
     *            value to match or null
     * @return true if all non-null parameters are equal to these of this statement
     */
    public boolean matches(URI subjectUri2, URI property2, String value2) {
        if (subjectUri2 != null && !subjectUri2.equals(subjectURI)) {
            return false;
        }
        if (property2 != null && !property2.equals(propertyURI)) {
            return false;
        }
        if (value2 != null && !value2.equals(object)) {
            return false;
        }
        return true;
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
        RDFNode objectNode = null;
        if (Utils.isAbsoluteURI(object)) {
            objectNode = model.createResource(URI.create(object).toString());
        } else {
            objectNode = model.createTypedLiteral(object);
        }
        return model.createStatement(subject, property, objectNode);
    }


    @Override
    public String toString() {
        return subjectValue + " " + propertyURI + " " + object;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((propertyURI == null) ? 0 : propertyURI.hashCode());
        result = prime * result + ((subjectValue == null) ? 0 : subjectValue.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Statement other = (Statement) obj;
        if (object == null) {
            if (other.object != null) {
                return false;
            }
        } else if (!object.equals(other.object)) {
            return false;
        }
        if (propertyURI == null) {
            if (other.propertyURI != null) {
                return false;
            }
        } else if (!propertyURI.equals(other.propertyURI)) {
            return false;
        }
        if (subjectValue == null) {
            if (other.subjectValue != null) {
                return false;
            }
        } else if (!subjectValue.equals(other.subjectValue)) {
            return false;
        }
        return true;
    }

}
