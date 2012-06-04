/**
 * 
 */
package org.purl.wf4ever.rosrs.client.common;

import java.io.Serializable;

/**
 * A class that extends {@link com.hp.hpl.jena.rdf.model.AnonId} in that it's serializable.
 * 
 * @author piotrekhol
 * 
 */
public class AnonId extends com.hp.hpl.jena.rdf.model.AnonId implements Serializable {

    /**
     * Constructor.
     * 
     * @param id
     *            Jena anonId
     */
    public AnonId(com.hp.hpl.jena.rdf.model.AnonId id) {
        super(id.getLabelString());
    }


    /** id. */
    private static final long serialVersionUID = 5382206987065355069L;

}
