package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;

import pl.psnc.dl.wf4ever.vocabulary.FOAF;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * A rough representation of a foaf:Person.
 * 
 * @author piotrekhol
 * 
 */
public class Person implements Serializable {

    /** id. */
    private static final long serialVersionUID = -6956273744325435068L;

    /** URI used in metadata. */
    private final URI uri;

    /** Human-friendly name. */
    private final String name;


    /**
     * Constructor.
     * 
     * @param uri
     *            URI used in metadata
     * @param name
     *            human-friendly name
     */
    public Person(URI uri, String name) {
        this.uri = uri;
        this.name = name;
    }


    public URI getUri() {
        return uri;
    }


    public String getName() {
        return name;
    }


    /**
     * Create a Person. The name is looked for in the resource's model.
     * 
     * @param resource
     *            Jena resource of the foaf:Agent
     * @return a {@link Person} or null if the parameter is null. If there is no name, "Unknown" is set.
     */
    public static Person create(RDFNode resource) {
        if (resource == null || !resource.isURIResource()) {
            return null;
        }
        Statement stmt = resource.asResource().getProperty(FOAF.name);
        return create(resource, stmt != null ? stmt.getObject() : null);
    }


    /**
     * Create a Person.
     * 
     * @param resource
     *            Jena resource of the foaf:Agent
     * @param nameNode
     *            Jena resource of the name
     * @return a {@link Person} or null if the resource parameter is null. If there is no name, "Unknown" is set.
     */
    public static Person create(RDFNode resource, RDFNode nameNode) {
        if (resource == null || !resource.isURIResource()) {
            return null;
        }
        if (nameNode != null && nameNode.isLiteral()) {
            return new Person(URI.create(resource.asResource().getURI()), nameNode.asLiteral().getString());
        } else {
            return new Person(URI.create(resource.asResource().getURI()), "Unknown");
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
        Person other = (Person) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return name + " (" + uri + ")";
    }
}
