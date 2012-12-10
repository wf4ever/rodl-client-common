package org.purl.wf4ever.rosrs.client.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;

import pl.psnc.dl.wf4ever.vocabulary.ORE;
import pl.psnc.dl.wf4ever.vocabulary.RO;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.sun.jersey.api.client.ClientResponse;

/**
 * ro:ResearchObject.
 * 
 * @author piotrekhol
 * 
 */
public class ResearchObject implements Serializable {

    /** id. */
    private static final long serialVersionUID = -2279202661374054080L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ResearchObject.class);

    /** URI. */
    private final URI uri;

    /** ROSRS client. */
    private final ROSRService rosrs;

    /** has the RO been loaded from ROSRS. */
    private boolean loaded;

    /** aggregated ro:Resources, excluding ro:Folders. */
    private Set<Resource> resources;

    /** aggregated ro:Folders. */
    private Set<Folder> folders;


    /**
     * Constructor.
     * 
     * @param uri
     *            RO URI
     * @param rosrs
     *            ROSRS client
     */
    public ResearchObject(URI uri, ROSRService rosrs) {
        this.uri = uri;
        this.rosrs = rosrs;
        this.loaded = false;
    }


    /**
     * Create a new Research Object.
     * 
     * @param rosrs
     *            ROSRS client
     * @param id
     *            RO id
     * @return the RO
     * @throws ROSRSException
     *             the creation failed
     */
    public static ResearchObject create(ROSRService rosrs, String id)
            throws ROSRSException {
        ClientResponse response = rosrs.createResearchObject(id);
        return new ResearchObject(response.getLocation(), rosrs);
    }


    public URI getUri() {
        return uri;
    }


    public ROSRService getRosrs() {
        return rosrs;
    }


    public boolean isLoaded() {
        return loaded;
    }


    /**
     * Load and parse the manifest.
     * 
     * @throws ROSRSException
     *             could not download the manifest
     * @throws ROException
     *             the manifest is incorrect
     */
    public void load()
            throws ROSRSException, ROException {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        InputStream is = rosrs.getResource(uri, RDFFormat.RDFXML.getDefaultMIMEType());
        try {
            model.read(is, uri.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.warn("Failed to close the manifest input stream", e);
            }
        }
        this.resources = extractResources(model);
    }


    /**
     * Identify ro:Resources aggregated by the RO.
     * 
     * @param model
     *            manifest model
     * @return a set of resources (not loaded)
     */
    private Set<Resource> extractResources(OntModel model) {
        Set<Resource> resources2 = new HashSet<>();
        String queryString = String
                .format(
                    "PREFIX ore: <%s> PREFIX ro: <%s> SELECT ?resource ?proxy WHERE { <%s> ore:aggregates ?resource . ?resource a ro:Resource . ?proxy ore:isProxyFor ?resource .",
                    ORE.NAMESPACE, RO.NAMESPACE, uri.toString());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qe.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.next();
                RDFNode r = solution.get("resource");
                if (r.as(Individual.class).hasRDFType(RO.Folder)) {
                    continue;
                }
                RDFNode p = solution.get("proxy");
                resources2.add(new Resource(this, URI.create(r.asResource().getURI()), URI.create(p.asResource()
                        .getURI())));
            }
        } finally {
            qe.close();
        }

        return resources2;
    }
}
