package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.purl.wf4ever.rosrs.client.evo.ROEVOService;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class BaseTest {

    /** ro evo service. */
    protected static ROEVOService roevo;

    /** ROSR service. */
    protected static ROSRService rosrs;

    /** Some RO available by HTTP. */
    protected static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some RO available by HTTP. */
    protected static final URI MOCK_MANIFEST = URI.create("http://localhost:8089/ro1/.ro/manifest.rdf");

    /** ROs to delete after a test. */
    protected static List<ResearchObject> rosToDelete = new ArrayList<>();

    /** The Live RO. */
    protected ResearchObject ro;


    /**
     * Create the Live RO.
     * 
     * @throws Exception
     *             unexpected response from RODL
     */
    @Before
    public void setUp()
            throws Exception {
        setUpRoResources();
        setUpRoCreateAndDelete();

        rosrs = new ROSRService(URI.create("http://localhost:8089/"), null);
    }


    protected void setUpRoCreateAndDelete() {
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
    }


    protected void setUpRoResources()
            throws IOException {
        InputStream manifest = getClass().getClassLoader().getResourceAsStream("ro1/.ro/manifest.rdf");
        stubFor(get(urlEqualTo("/ro1/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_MANIFEST.toString())));
        stubFor(get(urlEqualTo("/ro1/.ro/manifest.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(manifest))));
        InputStream body = getClass().getClassLoader().getResourceAsStream("ro1/body.rdf");
        stubFor(get(urlEqualTo("/ro1/body.rdf")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(body))));
        InputStream folder = getClass().getClassLoader().getResourceAsStream("ro1/folder1.rdf");
        stubFor(get(urlEqualTo("/ro1/folder1.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(folder))));
        InputStream folder2 = getClass().getClassLoader().getResourceAsStream("ro1/folder2.rdf");
        stubFor(get(urlEqualTo("/ro1/folder2.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(folder2))));
    }

}
