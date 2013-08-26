package org.purl.wf4ever.rosrs.client.search.dataclasses.solr;

import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.RangeFacet.Count;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

/**
 * Represents single date range facet and manufactures list of facet value.
 * 
 * @author pejot
 * 
 */
public class DateRangeFacetEntry extends FacetEntry {

    /** serialization. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     * 
     * @param rangeFacet
     *            facet field
     * @param name
     *            facet human-readable name
     */
    public DateRangeFacetEntry(RangeFacet<?, ?> rangeFacet, String name) {
        super();
        setDefault(true);
        this.fieldName = rangeFacet.getName();
        this.readableName = name;

        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calculateLabel(count), count.getCount(), fieldName, calculateQuery(count,
                rangeFacet.getGap().toString())));
        }
    }


    /**
     * Calculate query.
     * 
     * @param count
     *            count
     * @param gap
     *            gap
     * @return solr query
     */
    private String calculateQuery(Count count, String gap) {
        return fieldName + ":[" + count.getValue() + " TO " + count.getValue() + gap + "]";
    }


    /**
     * Calculate label.
     * 
     * @param count
     *            count
     * @return label
     */
    private String calculateLabel(Count count) {
        DateTime timeBegin = new DateTime(count.getValue());
        DateTime timeEnd = new DateTime(count.getValue());
        timeEnd = timeEnd.plusMonths(3);
        return timeBegin.toString().substring(0, 7) + " - " + timeEnd.toString().substring(0, 7);
    }
}
