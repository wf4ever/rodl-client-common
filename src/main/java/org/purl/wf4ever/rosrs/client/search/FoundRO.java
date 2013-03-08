/**
 * 
 */
package org.purl.wf4ever.rosrs.client.search;

import java.io.Serializable;

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


    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }
}
