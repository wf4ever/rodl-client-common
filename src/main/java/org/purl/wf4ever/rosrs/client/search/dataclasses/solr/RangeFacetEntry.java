package org.purl.wf4ever.rosrs.client.search.dataclasses.solr;

import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.RangeFacet.Count;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

/**
 * Represents single range facet and manufactures list of facet value.
 * 
 * @author pejot
 * 
 */
public class RangeFacetEntry extends FacetEntry {

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** solr facet. */
    private RangeFacet<?, ?> rangeFacet;


    /**
     * Constructor.
     * 
     * @param facet
     *            facet
     */
    public RangeFacetEntry(FacetEntry facet) {
        super();
    }


    /**
     * Constructor.
     * 
     * @param rangeFacet
     *            solr facet
     * @param name
     *            human readable name
     * @param numFound
     *            the number of found resources
     */
    public RangeFacetEntry(RangeFacet<?, ?> rangeFacet, String name, long numFound) {
        super();
        this.fieldName = rangeFacet.getName();
        this.rangeFacet = rangeFacet;
        this.readableName = name;
        Integer allResourcesNumber = (int) numFound;
        Integer clasifiedResourcesNumber = 0;
        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calculateName(count), count.getCount(), fieldName, calculateQuery(count)));
            clasifiedResourcesNumber += count.getCount();
        }
        values.add(new FacetValue(calculateLastName(), allResourcesNumber - clasifiedResourcesNumber, fieldName,
                calculateLastQuery()));
    }


    /**
     * Calculate query.
     * 
     * @param count
     *            count
     * @return query
     */
    private String calculateQuery(Count count) {
        Integer from = new Integer(count.getValue());
        Integer to = from + new Integer(rangeFacet.getGap().toString());
        to -= 1;
        return fieldName + ":[" + from.toString() + " TO " + to.toString() + "]";
    }


    /**
     * Calculate name of the last facet.
     * 
     * @return the name of the last facet
     */
    private String calculateLastName() {
        return "more than " + rangeFacet.getEnd().toString();
    }


    /**
     * String calcualte query for the last facet.
     * 
     * @return the query for last facet
     */
    private String calculateLastQuery() {
        return fieldName + ":[" + rangeFacet.getEnd().toString() + " TO " + "*" + "]";
    }


    /**
     * Calculate name.
     * 
     * @param count
     *            solr count
     * @return name
     */
    private String calculateName(Count count) {
        Integer from = new Integer(count.getValue());
        Integer to = from + new Integer(rangeFacet.getGap().toString());
        return from.toString() + " - " + to.toString();
    }
}
