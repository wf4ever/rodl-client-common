package org.purl.wf4ever.rosrs.client.evo;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.openrdf.rio.RDFFormat;

import com.damnhandy.uri.template.UriTemplate;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.Client;

/**
 * RO evolution service.
 * 
 * @author piotrekhol
 * 
 */
public class ROEVOService {

    /** RODL access token. */
    private String token;

    /** web client. */
    private transient Client client;

    /** URI of the copy service. */
    private URI copyUri;

    /** URI of the finalize service. */
    private URI finalizeUri;

    /** URI template of the evo info service. The first param must be replaced with the RO URI. */
    private UriTemplate infoUriTemplate;


    /**
     * Constructor.
     * 
     * @param roevoUri
     *            ROEVO URI
     * @param token
     *            RODL access token
     */
    public ROEVOService(URI roevoUri, String token) {
        this.token = token;
        InputStream serviceDesc = getClient().resource(roevoUri).accept(RDFFormat.RDFXML.getDefaultMIMEType())
                .get(InputStream.class);
        Model model = ModelFactory.createDefaultModel();
        model.read(serviceDesc, roevoUri.toString());
        Resource roevo = model.getResource(roevoUri.toString());
        this.copyUri = URI.create(roevo.getPropertyResourceValue(pl.psnc.dl.wf4ever.vocabulary.ROEVOService.copy)
                .getURI());
        this.finalizeUri = URI.create(roevo.getPropertyResourceValue(
            pl.psnc.dl.wf4ever.vocabulary.ROEVOService.finalize).getURI());
        this.infoUriTemplate = UriTemplate.fromTemplate(roevo
                .listProperties(pl.psnc.dl.wf4ever.vocabulary.ROEVOService.info).next().getObject().asLiteral()
                .getString());
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


    public JobStatus createSnapshot(URI roUri, String target, boolean finalize) {
        JobStatus statusIn = new JobStatus(roUri, EvoType.SNAPSHOT, finalize);
        statusIn.setTarget(target);
        JobStatus statusOut = getClient().resource(copyUri).type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE).post(JobStatus.class, statusIn);
        return statusOut;
    }
}
