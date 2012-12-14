package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

public class Annotation {

    private ResearchObject researchObject;
    private URI uri;
    private URI body;
    private Set<URI> targets;
    private URI creator;
    private DateTime created;


    public Annotation(ResearchObject researchObject, URI uri, URI body, Set<URI> targets, URI creator, DateTime created) {
        this.researchObject = researchObject;
        this.uri = uri;
        this.body = body;
        this.targets = targets;
        this.creator = creator;
        this.created = created;
    }


    public Annotation(ResearchObject researchObject, URI uri, URI body, URI target, URI creator, DateTime created) {
        this(researchObject, uri, body, new HashSet<URI>(Arrays.asList(new URI[] { target })), creator, created);
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public URI getUri() {
        return uri;
    }


    public URI getBody() {
        return body;
    }


    public URI getCreator() {
        return creator;
    }


    public DateTime getCreated() {
        return created;
    }


    public Set<URI> getTargets() {
        return targets;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }


    @Override
    public String toString() {
        return uri.toString();
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
        Annotation other = (Annotation) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
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
