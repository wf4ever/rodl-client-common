package org.purl.wf4ever.rosrs.client.search.dataclasses.solr;

import java.util.ArrayList;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

/**
 * Facet which represents creator field.
 * 
 * @author pejot
 * 
 */
public class CreatorFacetEntry extends FacetEntry {

    /** Serialization. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     * 
     * @param field
     *            facet field
     * @param name
     *            human readable name
     */
    public CreatorFacetEntry(FacetField field, String name) {
        super();
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
    public CreatorFacetEntry(FacetField field, String name, boolean sorteable) {
        this(field, name);
        this.sorteable = sorteable;
    }

}
