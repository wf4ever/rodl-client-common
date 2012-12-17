package org.purl.wf4ever.rosrs.client.common;

import java.io.IOException;
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
 * ro:Folder.
 * 
 * @author piotrekhol
 * 
 */
public class Folder extends Resource {

    /** id. */
    private static final long serialVersionUID = 8014407879648172595L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Folder.class);

    /** Resource map (graph with folder description) URI. */
    private URI resourceMap;

    /** has the resource map been loaded. */
    private boolean loaded;

    /** is the folder a root folder in the RO. */
    private boolean rootFolder;

    /** folder entries (folder content). */
    private Set<FolderEntry> folderEntries;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            The RO it is aggregated by
     * @param uri
     *            resource URI
     * @param proxyURI
     *            URI of the proxy
     * @param resourceMap
     *            Resource map (graph with folder description) URI
     * @param creator
     *            author of the resource
     * @param created
     *            creation date
     * @param rootFolder
     *            is the folder a root folder in the RO
     */
    public Folder(ResearchObject researchObject, URI uri, URI proxyURI, URI resourceMap, URI creator, DateTime created,
            boolean rootFolder) {
        super(researchObject, uri, proxyURI, creator, created);
        this.resourceMap = resourceMap;
        this.rootFolder = rootFolder;
        this.loaded = false;
    }


    /**
     * Load the folder contents from the resource map.
     * 
     * @throws ROSRSException
     *             unexpected service response
     */
    public void load()
            throws ROSRSException {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        if (!FileManager.get().mapURI(resourceMap.toString()).startsWith("http")) {
            FileManager.get().readModel(model, resourceMap.toString(), resourceMap.toString(), "RDF/XML");
        } else {
            ClientResponse response = researchObject.getRosrs().getResource(resourceMap, "application/rdf+xml");
            try {
                model.read(response.getEntityInputStream(), resourceMap.toString());
            } finally {
                try {
                    response.getEntityInputStream().close();
                } catch (IOException e) {
                    LOG.warn("Failed to close the resource map input stream", e);
                }
            }
        }
        this.folderEntries = extractFolderEntries(model);
        this.loaded = true;
    }


    /**
     * Identify all the folder entries aggregated by the folder.
     * 
     * @param model
     *            resource map model
     * @return a set of folder entries
     */
    private Set<FolderEntry> extractFolderEntries(OntModel model) {
        Set<FolderEntry> folderEntries2 = new HashSet<>();
        String queryString = String
                .format(
                    "PREFIX ore: <%s> PREFIX dcterms: <%s> PREFIX ro: <%s> SELECT ?resource ?proxy ?created ?creator WHERE { <%s> ore:aggregates ?resource . ?resource a ro:Resource . ?proxy ore:proxyFor ?resource . OPTIONAL { ?resource dcterms:creator ?creator . } OPTIONAL { ?resource dcterms:created ?created . } }",
                    ORE.NAMESPACE, DCTerms.NS, RO.NAMESPACE, uri.toString());

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
                URI rURI = URI.create(r.asResource().getURI());
                RDFNode p = solution.get("proxy");
                RDFNode creatorNode = solution.get("creator");
                URI resCreator = creatorNode != null && creatorNode.isURIResource() ? URI.create(creatorNode
                        .asResource().getURI()) : null;
                RDFNode createdNode = solution.get("created");
                DateTime resCreated = createdNode != null && createdNode.isLiteral() ? DateTime.parse(createdNode
                        .asLiteral().getString()) : null;
                resources2.put(rURI, new Resource(this, rURI, URI.create(p.asResource().getURI()), resCreator,
                        resCreated));
            }
        } finally {
            qe.close();
        }

        return resources2;
    }


    public URI getResourceMap() {
        return resourceMap;
    }


    public boolean isLoaded() {
        return loaded;
    }


    public boolean isRootFolder() {
        return rootFolder;
    }


    public Set<FolderEntry> getFolderEntries() {
        return folderEntries;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((proxyUri == null) ? 0 : proxyUri.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        result = prime * result + ((resourceMap == null) ? 0 : resourceMap.hashCode());
        result = prime * result + (rootFolder ? 1231 : 1237);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Folder other = (Folder) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
        if (proxyUri == null) {
            if (other.proxyUri != null) {
                return false;
            }
        } else if (!proxyUri.equals(other.proxyUri)) {
            return false;
        }
        if (resourceMap == null) {
            if (other.resourceMap != null) {
                return false;
            }
        } else if (!resourceMap.equals(other.resourceMap)) {
            return false;
        }
        if (rootFolder != other.rootFolder) {
            return false;
        }
        return true;
    }

}
