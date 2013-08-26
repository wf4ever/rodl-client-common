package org.purl.wf4ever.rosrs.client.search.dataclasses.solr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

/**
 * Represents single simple facet and manufactures list of facet value.
 * 
 * @author pejot
 * 
 */
public class FacetEntry implements Serializable {

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** field name. */
    protected String fieldName;
    /** human readable facet name. */
    protected String readableName;
    /** list of facet values. */
    protected List<FacetValue> values;
    /** can this facet be used for sorting, default true. */
    protected boolean sorteable = true;

    /** Should this facet be default when sorting. */
    private boolean defaults = false;


    /**
     * Constructor.
     */
    public FacetEntry() {
        this.values = new ArrayList<FacetValue>();
    }


    /**
     * Constructor.
     * 
     * @param field
     *            facet field
     * @param name
     *            human readable name
     */
    public FacetEntry(FacetField field, String name) {
        this();
        this.readableName = name;
        this.fieldName = field.getName();
        this.values = new ArrayList<FacetValue>();
        if (field != null) {
            for (Count count : field.getValues()) {
                String query = fieldName + ":" + "\"" + count.getName() + "\"";
                values.add(new FacetValue(count.getName(), (int) count.getCount(), fieldName, query));
            }
        }

    }


    /**
     * Constructor.
     * 
     * @param field
     *            facet field
     * @param name
     *            human readable name
     * @param sorteable
     *            can this facet be used for sorting
     */
    public FacetEntry(FacetField field, String name, boolean sorteable) {
        this(field, name);
        this.sorteable = sorteable;
    }


    public List<FacetValue> getValues() {
        return this.values;
    }


    public String getFieldName() {
        return fieldName;
    }


    public String getName() {
        return readableName;
    }


    public boolean isSorteable() {
        return sorteable;
    }


    public void setSorteable(boolean sorteable) {
        this.sorteable = sorteable;
    }


    public boolean isDefault() {
        return defaults;
    }


    public void setDefault(boolean defaults) {
        this.defaults = defaults;
    }
}
