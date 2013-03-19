/**
 * 
 */
package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;
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
    private DateTime created;
    /** creator field from solr index. */
    private List<String> creators;


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
    public FoundRO(ResearchObject researchObject, double score, int numberOfResources, int numberOfAnnotations,
            String status, DateTime created, List<String> creators) {
        this.researchObject = researchObject;
        this.score = score;
        this.numberOfResources = numberOfResources;
        this.numberOfAnnotations = numberOfAnnotations;
        this.status = status;
        this.created = created;
        this.creators = creators;
    }


    /**
     * Constructor.
     * 
     * @param researchObject
     *            RO
     * @param score
     *            search score
     */
    public FoundRO(ResearchObject researchObject, double score) {
        this.researchObject = researchObject;
        this.score = score;
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


    public Integer getNumberOfResources() {
        return numberOfResources;
    }


    public Integer getNumberOfAnnotations() {
        return numberOfAnnotations;
    }


    public String getStatus() {
        return status;
    }


    public DateTime getCreated() {
        return created;
    }


    public List<String> getCreators() {
        return creators;
    }

}
