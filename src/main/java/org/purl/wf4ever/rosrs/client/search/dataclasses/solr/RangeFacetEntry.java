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
        this.readableName = name;

        Integer allResourcesNumber = (int) numFound;
        Integer clasifiedResourcesNumber = 0;
        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calculateName(count, rangeFacet), count.getCount(), fieldName, calculateQuery(
                count, rangeFacet)));
            clasifiedResourcesNumber += count.getCount();
        }
        values.add(new FacetValue(calculateLastName(rangeFacet), allResourcesNumber - clasifiedResourcesNumber,
                fieldName, calculateLastQuery(rangeFacet)));
    }


    /**
     * Calculate query.
     * 
     * @param count
     *            count
     * @param facet
     *            range facet
     * @return query
     */
    private String calculateQuery(Count count, RangeFacet<?, ?> facet) {
        Integer from = new Integer(count.getValue());
        Integer to = from + (Integer) facet.getGap();
        to -= 1;
        return fieldName + ":[" + from.toString() + " TO " + to.toString() + "]";
    }


    /**
     * Calculate name of the last facet.
     * 
     * @param facet
     *            range facet
     * @return the name of the last facet
     */
    private String calculateLastName(RangeFacet<?, ?> facet) {
        return "more than " + facet.getEnd().toString();
    }


    /**
     * String calcualte query for the last facet.
     * 
     * @param facet
     *            range facet
     * @return the query for last facet
     */
    private String calculateLastQuery(RangeFacet<?, ?> facet) {
        return fieldName + ":[" + facet.getEnd().toString() + " TO " + "*" + "]";
    }


    /**
     * Calculate name.
     * 
     * @param count
     *            solr count
     * @param facet
     *            range facet
     * @return name
     */
    private String calculateName(Count count, RangeFacet<?, ?> facet) {
        Integer from = new Integer(count.getValue());
        Integer to = from + (Integer) facet.getGap();
        return from.toString() + " - " + to.toString();
    }
}
