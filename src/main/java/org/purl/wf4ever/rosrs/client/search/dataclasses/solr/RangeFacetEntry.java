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
     */
    public RangeFacetEntry(RangeFacet<?, ?> rangeFacet, String name) {
        super();
        this.fieldName = rangeFacet.getName();
        this.rangeFacet = rangeFacet;
        this.readableName = name;
        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calcualteName(count), count.getCount(), fieldName, calculateQuery(count)));
        }
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
        return fieldName + ":[" + from.toString() + " TO " + to.toString() + "]";
    }


    /**
     * Calculate name.
     * 
     * @param count
     *            solr count
     * @return name
     */
    private String calcualteName(Count count) {
        Integer from = new Integer(count.getValue());
        Integer to = from + new Integer(rangeFacet.getGap().toString());
        return from.toString() + " - " + to.toString();
    }
}
