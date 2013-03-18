/**
 * 
 */
package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * RODL search service result.
 * 
 * @author piotrhol
 * 
 */
public class FoundRO implements Serializable {

    /** id. */
    private static final long serialVersionUID = -9046167750816266548L;

    /** RO found. */
    private final ResearchObject researchObject;

    /** Number of resources in the RO. */
    private int resourceCount;

    /** Search score. The higher the better, from 0 to infinity. */
    private final double score;
    /** resources_size field from solr index. */
    private Integer numberOfResources;
    /** annotations_size field from solr index. */
    private Integer numberOfAnnotations;
    /** evo_Type field from solr index. */
    private String status;
    /** created field from solr index. */
    private Date created;
    /** creator field from solr index. */
    private ArrayList<String> creators;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            RO
     * @param score
     *            search score
     * @param numberOfResources
     *            resources_size field from solr index
     * @param numberOfAnnotations
     *            annotations_size field from solr index
     * @param status
     *            evo_Type field from solr index
     * @param created
     *            created field from solr index
     * @param creators
     *            creator field from solr index
     */
    public FoundRO(ResearchObject researchObject, double score, Object numberOfResources, Object numberOfAnnotations,
            Object status, Object created, Object creators) {
        this.researchObject = researchObject;
        this.score = score;

        try {
            this.numberOfResources = (Integer) numberOfResources;
        } catch (ClassCastException e) {
            this.numberOfResources = null;
        }
        try {
            this.numberOfAnnotations = (Integer) numberOfAnnotations;
        } catch (ClassCastException e) {
            this.numberOfAnnotations = null;
        }
        try {
            this.status = (String) status;
        } catch (ClassCastException e) {
            this.status = null;
        }
        try {
            this.created = (Date) created;
        } catch (ClassCastException e) {
            this.created = null;
        }
        try {
            this.creators = (ArrayList<String>) creators;
        } catch (ClassCastException e) {
            this.creators = null;
        }
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public double getScore() {
        return score;
    }


    public int getScoreInPercent() {
        return (int) Math.round(score * 100);
    }


    public int getResourceCount() {
        return resourceCount;
    }


    public Date getCreated() {
        return created;
    }


    public ArrayList<String> getCreators() {
        return creators;
    }
}
