package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;

/**
 * The triple of label, count and query.
 * 
 * @author pejot
 * 
 */
public class FacetValue implements Serializable {

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** label. */
    private String label;
    /** count (number of results). */
    private Integer count;
    /** the original facet name. */
    private String paramName;
    /** solr query. */
    private String query;


    /**
     * Constructor.
     * 
     * @param label
     *            label
     * @param count
     *            count (number of results)
     * @param paramName
     *            the original facet name
     * @param query
     *            query
     */
    public FacetValue(String label, Integer count, String paramName, String query) {
        this.label = label;
        this.count = count;
        this.setParamName(paramName);
        this.setQuery(query);
    }


    public Integer getCount() {
        return count;
    }


    public void setCount(Integer count) {
        this.count = count;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getParamName() {
        return paramName;
    }


    public void setParamName(String paramName) {
        this.paramName = paramName;
    }


    public String getQuery() {
        return query;
    }


    public void setQuery(String query) {
        this.query = query;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FacetValue)) {
            return false;
        }
        FacetValue val = (FacetValue) obj;
        if (val.getParamName().equals(this.paramName) && val.getLabel().equals(this.getLabel())) {
            return true;
        }
        return false;
    }
}
