package org.purl.wf4ever.rosrs.client.common;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import pl.psnc.dl.wf4ever.vocabulary.ORE;
import pl.psnc.dl.wf4ever.vocabulary.RO;

import com.google.common.collect.Multimap;
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
     * Create a new folder.
     * 
     * @param researchObject
     *            research object by which the folder should be aggregated
     * @param path
     *            folder path relative to the RO
     * @return a folder instance, with creator, created or rootFolder properties unset
     * @throws ROSRSException
     *             unexpected response from the server
     */
    public static Folder create(ResearchObject researchObject, String path)
            throws ROSRSException {
        ClientResponse response = researchObject.getRosrs().createFolder(researchObject.getUri(), path);
        Multimap<String, URI> headers = Utils.getLinkHeaders(response.getHeaders().get("Link"));
        URI folder = headers.get("http://www.openarchives.org/ore/terms/proxyFor").isEmpty() ? null : headers
                .get("http://www.openarchives.org/ore/terms/proxyFor").iterator().next();
        URI resourceMap = headers.get("http://www.openarchives.org/ore/terms/isDescribedBy").iterator().next();
        response.close();
        return new Folder(researchObject, folder, response.getLocation(), resourceMap, null, null, false);
    }


    /**
     * Load the folder contents from the resource map.
     * 
     * @param recursive
     *            should the folder tree rooted in this folder be loaded as well
     * @throws ROSRSException
     *             unexpected service response
     */
    public void load(boolean recursive)
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
        if (recursive) {
            for (FolderEntry entry : folderEntries) {
                Folder folder = researchObject.getFolder(entry.getResource());
                if (folder != null && !folder.isLoaded()) {
                    folder.load(true);
                }
            }
        }
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
                    "PREFIX ore: <%s> PREFIX ro: <%s> SELECT ?entry ?resource ?name WHERE { <%s> ore:aggregates ?resource . ?entry a ro:FolderEntry ; ore:proxyIn <%<s> ; ore:proxyFor ?resource . OPTIONAL { ?entry ro:entryName ?name . } }",
                    ORE.NAMESPACE, RO.NAMESPACE, uri.toString());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qe.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.next();
                RDFNode e = solution.get("entry");
                URI eURI = URI.create(e.asResource().getURI());
                RDFNode r = solution.get("resource");
                URI rURI = URI.create(r.asResource().getURI());
                RDFNode n = solution.get("name");
                String name = n != null ? n.asLiteral().getString() : null;
                folderEntries2.add(new FolderEntry(this, eURI, rURI, name));
            }
        } finally {
            qe.close();
        }

        return folderEntries2;
    }


    /**
     * Add a new folder entry to this folder.
     * 
     * @param resource
     *            the resource to aggregate
     * @param entryName
     *            entry name or null
     * @return the new folder entry
     * @throws ROSRSException
     *             unexpected response from the server
     * @throws ROException
     *             the response entity body is incorrect
     */
    public FolderEntry addEntry(Resource resource, String entryName)
            throws ROSRSException, ROException {
        ClientResponse response = this.researchObject.getRosrs().addFolderEntry(uri, resource.getUri(), entryName);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
        model.read(response.getEntityInputStream(), response.getLocation().toString());
        List<Individual> entries = model.listIndividuals(RO.FolderEntry).toList();
        if (entries.isEmpty()) {
            throw new ROException("The create folder entry response contains no folder entries",
                    researchObject.getUri());
        }
        String name = entries.get(0).getPropertyValue(RO.entryName).asLiteral().getString();
        FolderEntry entry = new FolderEntry(this, response.getLocation(), resource.getUri(), name);
        this.folderEntries.add(entry);
        return entry;
    }


    /**
     * Create a new subfolder.
     * 
     * @param name
     *            folder name, relative to this folder path
     * @return a new folder entry
     * @throws ROSRSException
     *             unexpected response from the server
     * @throws ROException
     *             the response entity body is incorrect
     */
    public FolderEntry addSubFolder(String name)
            throws ROSRSException, ROException {
        URI subfolderURI = UriBuilder.fromUri(uri).path(name).build();
        String path = this.researchObject.getUri().relativize(subfolderURI).toString();
        Folder subfolder = this.researchObject.createFolder(path);
        return addEntry(subfolder, name);
    }


    /**
     * Delete the folder from ROSRS and from the research object.
     * 
     * @throws ROSRSException
     *             server returned an unexpected response
     */
    public void delete()
            throws ROSRSException {
        researchObject.getRosrs().deleteResource(uri);
        researchObject.removeFolder(this);
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
