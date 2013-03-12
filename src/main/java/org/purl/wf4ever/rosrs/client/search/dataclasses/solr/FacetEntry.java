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


    public List<FacetValue> getValues() {
        return this.values;
    }


    public String getFieldName() {
        return fieldName;
    }


    public String getName() {
        return readableName;
    }

}
