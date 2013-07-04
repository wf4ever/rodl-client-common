package org.purl.wf4ever.rosrs.client;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;

/**
 * Base of all resources.
 * 
 * @author piotrekhol
 * 
 */
public class Thing implements Serializable {

    /** id. */
    private static final long serialVersionUID = -6086301275622387040L;

    /** Date format: Tuesday 14:03. */
    public static final SimpleDateFormat SDF1 = new SimpleDateFormat("EEEE HH:mm");

    /** Date format: 24 05 2012 23:04. */
    public static final SimpleDateFormat SDF2 = new SimpleDateFormat("dd MMMM yyyy HH:mm");

    /** resource URI. */
    protected final URI uri;

    /** last segment of URI or full URI if no path exists. */
    protected final String name;

    /** creator URI. */
    protected Person creator;

    /** creation date. */
    protected DateTime created;


    /**
     * Constructor.
     * 
     * @param uri
     *            resource URI
     * @param created
     *            creation date
     * @param creator
     *            creator URI and name
     */
    public Thing(URI uri, Person creator, DateTime created) {
        this.uri = uri;
        this.creator = creator;
        this.created = created;
        this.name = calculateName();
    }


    public URI getUri() {
        return uri;
    }


    public DateTime getCreated() {
        return created;
    }


    /**
     * Return the creation date nicely formatted.
     * 
     * @return the date formatted
     */
    public String getCreatedFormatted() {
        if (getCreated() != null) {
            if (getCreated().compareTo(new DateTime().minusWeeks(1)) > 0) {
                return SDF1.format(getCreated().toDate());
            } else {
                return SDF2.format(getCreated().toDate());
            }

        } else {
            return null;
        }
    }


    /**
     * Returns the last segment of the resource path, or the whole URI if has no path.
     * 
     * @return name or null
     */
    public String calculateName() {
        if (uri == null) {
            return null;
        }
        if (uri.getPath() == null || uri.getPath().isEmpty() || uri.getPath().equals("/")) {
            return uri.toString();
        }
        String[] segments = uri.getPath().split("/");
        String name2 = segments[segments.length - 1];
        if (uri.getPath().endsWith("/")) {
            name2 = name2.concat("/");
        }
        return name2;
    }


    public String getName() {
        return name;
    }


    public void setCreated(DateTime created) {
        this.created = created;
    }


    public Person getAuthor() {
        return creator;
    }


    public void setAuthor(Person author) {
        this.creator = author;
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
        Thing other = (Thing) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

}
