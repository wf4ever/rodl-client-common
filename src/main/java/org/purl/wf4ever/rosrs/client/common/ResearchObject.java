package org.purl.wf4ever.rosrs.client.common;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

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
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DCTerms;
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

    /** creator URI. */
    private URI creator;

    /** creation date. */
    private DateTime created;


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
        if (!FileManager.get().mapURI(uri.toString()).startsWith("http")) {
            FileManager.get().readModel(model, uri.toString(), uri.resolve(".ro/manifest.rdf").toString(), "RDF/XML");
        } else {
            ClientResponse response = rosrs.getResource(uri, "application/rdf+xml");
            try {
                model.read(response.getEntityInputStream(), response.getLocation().toString());
            } finally {
                try {
                    response.getEntityInputStream().close();
                } catch (IOException e) {
                    LOG.warn("Failed to close the manifest input stream", e);
                }
            }
        }
        this.creator = extractCreator(model);
        this.created = extractCreated(model);
        this.resources = extractResources(model);
        this.folders = extractFolders(model);
        this.loaded = true;
    }


    public Set<Resource> getResources() {
        return resources;
    }


    public Set<Folder> getFolders() {
        return folders;
    }


    public URI getCreator() {
        return creator;
    }


    public DateTime getCreated() {
        return created;
    }


    /**
     * Find the dcterms:creator of the RO.
     * 
     * @param model
     *            manifest model
     * @return creator URI or null if not defined
     * @throws ROException
     *             incorrect manifest
     */
    private URI extractCreator(OntModel model)
            throws ROException {
        Individual ro = model.getIndividual(uri.toString());
        if (ro == null) {
            throw new ROException("RO not found in the manifest", uri);
        }
        com.hp.hpl.jena.rdf.model.Resource c = ro.getPropertyResourceValue(DCTerms.creator);
        return c != null ? URI.create(c.getURI()) : null;
    }


    /**
     * Find the dcterms:created date of the RO.
     * 
     * @param model
     *            manifest model
     * @return creation date or null if not defined
     * @throws ROException
     *             incorrect manifest
     */
    private DateTime extractCreated(OntModel model)
            throws ROException {
        Individual ro = model.getIndividual(uri.toString());
        if (ro == null) {
            throw new ROException("RO not found in the manifest", uri);
        }
        RDFNode d = ro.getPropertyValue(DCTerms.created);
        if (d == null || !d.isLiteral()) {
            return null;
        }
        return DateTime.parse(d.asLiteral().getString());
    }


    /**
     * Identify ro:Resources that are not ro:Folders, aggregated by the RO.
     * 
     * @param model
     *            manifest model
     * @return a set of resources (not loaded)
     */
    private Set<Resource> extractResources(OntModel model) {
        Set<Resource> resources2 = new HashSet<>();
        model.write(System.out, "TURTLE");
        String queryString = String
                .format(
                    "PREFIX ore: <%s> PREFIX ro: <%s> SELECT ?resource ?proxy WHERE { <%s> ore:aggregates ?resource . ?resource a ro:Resource . ?proxy ore:proxyFor ?resource . }",
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


    /**
     * Identify ro:Resources that are not ro:Folders, aggregated by the RO.
     * 
     * @param model
     *            manifest model
     * @return a set of folders (not loaded)
     */
    private Set<Folder> extractFolders(OntModel model) {
        Set<Folder> folders2 = new HashSet<>();
        String queryString = String
                .format(
                    "PREFIX ore: <%s> PREFIX ro: <%s> SELECT ?folder ?proxy ?resourcemap WHERE { <%s> ore:aggregates ?folder . ?folder a ro:Folder ; ore:isDescribedBy ?resourcemap . ?proxy ore:proxyFor ?folder . }",
                    ORE.NAMESPACE, RO.NAMESPACE, uri.toString());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qe.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.next();
                RDFNode f = solution.get("folder");
                RDFNode p = solution.get("proxy");
                RDFNode rm = solution.get("resourcemap");
                folders2.add(new Folder(this, URI.create(f.asResource().getURI()), URI.create(p.asResource().getURI()),
                        URI.create(rm.asResource().getURI())));
            }
        } finally {
            qe.close();
        }

        return folders2;
    }
}
