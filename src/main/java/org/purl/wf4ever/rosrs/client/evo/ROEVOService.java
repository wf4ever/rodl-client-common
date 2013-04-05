package org.purl.wf4ever.rosrs.client.evo;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;

import com.damnhandy.uri.template.UriTemplate;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * RO evolution service.
 * 
 * @author piotrekhol
 * 
 */
public class ROEVOService implements Serializable {

    /** id. */
    private static final long serialVersionUID = -8999917522891485806L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ROEVOService.class);

    /** RODL access token. */
    private String token;

    /** web client. */
    private transient Client client;

    /** URI of the copy service. */
    private URI copyUri;

    /** URI of the finalize service. */
    @SuppressWarnings("unused")
    private URI finalizeUri;

    /** URI template of the evo info service. The first param must be replaced with the RO URI. */
    private String infoUriTemplateString;

    /** Base RO EVO service URI. */
    private URI roevoUri;


    /**
     * Constructor.
     * 
     * @param roevoUri
     *            ROEVO URI
     * @param token
     *            RODL access token
     */
    public ROEVOService(URI roevoUri, String token) {
        this.roevoUri = roevoUri;
        this.token = token;
    }


    /**
     * Return the RO copy service URI. Load it from the RO EVO service description if necessary.
     * 
     * @return the RO copy service URI
     */
    private URI getCopyUri() {
        if (copyUri == null) {
            init();
        }
        return copyUri;
    }


    /**
     * Load the RO EVO service description.
     */
    private void init() {
        try {
            InputStream serviceDesc = getClient().resource(roevoUri).accept(RDFFormat.RDFXML.getDefaultMIMEType())
                    .get(InputStream.class);
            Model model = ModelFactory.createDefaultModel();
            model.read(serviceDesc, roevoUri.toString());
            Resource roevo = model.getResource(roevoUri.toString());
            this.copyUri = URI.create(roevo.listProperties(pl.psnc.dl.wf4ever.vocabulary.ROEVOService.copy).next()
                    .getObject().asLiteral().getString());
            this.finalizeUri = URI.create(roevo.listProperties(pl.psnc.dl.wf4ever.vocabulary.ROEVOService.finalize)
                    .next().getObject().asLiteral().getString());
            this.infoUriTemplateString = roevo.listProperties(pl.psnc.dl.wf4ever.vocabulary.ROEVOService.info).next()
                    .getObject().asLiteral().getString();
        } catch (JenaException e) {
            LOG.warn("Could not initialize the roevo service: " + e.getLocalizedMessage());
        }
    }


    /**
     * Return the RO evo info service URI. Load it from the RO EVO service description if necessary.
     * 
     * @return the RO evo info service URI
     */
    public UriTemplate getInfoUriTemplate() {
        if (infoUriTemplateString == null) {
            init();
        }
        return UriTemplate.fromTemplate(infoUriTemplateString);
    }


    /**
     * Return an HTTP client, creating it if necessary.
     * 
     * @return an HTTP client
     */
    private Client getClient() {
        if (client == null) {
            client = Client.create();
        }
        return client;
    }


    /**
     * Create a new snapshot RO.
     * 
     * @param roUri
     *            live RO URI
     * @param target
     *            snapshot identifier
     * @param finalize
     *            freeze the snapshot after creating?
     * @return a status of the snapshotting job, which can later be refreshed
     */
    public JobStatus createSnapshot(URI roUri, String target, boolean finalize) {
        return createImmutable(roUri, target, finalize, EvoType.SNAPSHOT);
    }


    /**
     * Create a new archive RO.
     * 
     * @param roUri
     *            live RO URI
     * @param target
     *            archive identifier
     * @param finalize
     *            freeze the archive after creating?
     * @return a status of the archival job, which can later be refreshed
     */
    public JobStatus createArchive(URI roUri, String target, boolean finalize) {
        return createImmutable(roUri, target, finalize, EvoType.ARCHIVE);
    }


    /**
     * Create a new archive or snapshot RO.
     * 
     * @param roUri
     *            live RO URI
     * @param target
     *            new RO identifier
     * @param finalize
     *            freeze the RO after creating?
     * @param evoType
     *            snapshot or archive
     * @return a status of the job, which can later be refreshed
     */
    private JobStatus createImmutable(URI roUri, String target, boolean finalize, EvoType evoType) {
        JobStatus statusIn = new JobStatus(roUri, evoType, finalize);
        ClientResponse response = getClient().resource(getCopyUri()).header("Slug", target)
                .header("Authorization", "Bearer " + token).type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, statusIn);
        JobStatus statusOut = response.getEntity(JobStatus.class);
        statusOut.setUri(response.getLocation());
        statusOut.setRoevo(this);
        response.close();
        return statusOut;
    }


    /**
     * Get a new status of a snapshotting/archival job.
     * 
     * @param jobUri
     *            URI of an existing job
     * @return the job status
     */
    public JobStatus getStatus(URI jobUri) {
        JobStatus statusOut = getClient().resource(jobUri).accept(MediaType.APPLICATION_JSON_TYPE).get(JobStatus.class);
        statusOut.setUri(jobUri);
        statusOut.setRoevo(this);
        return statusOut;
    }


    /**
     * Get the evolution information as a Turtle graph.
     * 
     * @param roUri
     *            URI of the RO that the information should be about
     * @return an input stream of an RDF graph in the Turtle format
     */
    public InputStream getEvolutionInformationInputStream(URI roUri) {
        return getClient().resource(getInfoUriTemplate().set("ro", roUri.toString()).expand())
                .accept(RDFFormat.TURTLE.getDefaultMIMEType()).get(InputStream.class);
    }

}
