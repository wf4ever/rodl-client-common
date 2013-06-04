package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.Before;

import pl.psnc.dl.wf4ever.vocabulary.ORE;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class BaseTest {

    /** ROSR service. */
    protected static ROSRService rosrs;

    /** Some RO available by HTTP. */
    protected static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some RO available by HTTP. */
    protected static final URI MOCK_MANIFEST = URI.create("http://localhost:8089/ro1/.ro/manifest.rdf");

    /** Some resource available by HTTP. */
    protected static final URI MOCK_RESOURCE = URI.create("http://localhost:8089/ro1/res1.txt");

    /** Some resource available by HTTP. */
    protected static final URI MOCK_RESOURCE_PROXY = URI.create("http://localhost:8089/resproxy");

    /** Some resource available by HTTP. */
    protected static final URI MOCK_EXT_RESOURCE_PROXY = URI.create("http://localhost:8089/extresproxy");

    /** A loaded RO. */
    protected ResearchObject ro1;

    /** URI of rdfs:comment. */
    protected static final URI RDFS_COMMENT = URI.create(RDFS.comment.getURI());

    /** A Person appearing in test data. */
    protected static final Person PERSON = new Person(URI.create("http://test.myopenid.com"), "Person");
    /** A Person appearing in test data. */
    protected static final Person PERSON_1 = new Person(URI.create("http://test1.myopenid.com"), "Person 1");
    /** A Person appearing in test data. */
    protected static final Person PERSON_2 = new Person(URI.create("http://test2.myopenid.com"), "Person 2");
    /** A Person appearing in test data. */
    protected static final Person PERSON_3 = new Person(URI.create("http://test3.myopenid.com"), "Person 3");


    /**
     * Set up a mockup HTTP server.
     * 
     * @throws Exception
     *             if there are any problem with test resources
     */
    @Before
    public void setUp()
            throws Exception {
        WireMock.resetAllScenarios();
        setUpRoResources();
        setUpRoCreateDelete();
        setUpResourceCreateDelete();

        rosrs = new ROSRService(URI.create("http://localhost:8089/"), null);
        ro1 = new ResearchObject(MOCK_RO, rosrs);
        ro1.load();
    }


    /**
     * Configure WireMock to handle creating the RO.
     */
    protected void setUpRoCreateDelete() {
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
    }


    /**
     * Configure WireMock to return resources from the ro1 folder.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
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
        stubFor(get(urlEqualTo("/ro1/body.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(body))));
        stubFor(put(urlEqualTo("/ro1/body.rdf")).willReturn(aResponse().withStatus(200)));
        InputStream folder = getClass().getClassLoader().getResourceAsStream("ro1/folder1.rdf");
        stubFor(get(urlEqualTo("/ro1/folder1.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(folder))));
        InputStream folder2 = getClass().getClassLoader().getResourceAsStream("ro1/folder2.rdf");
        stubFor(get(urlEqualTo("/ro1/folder2.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(folder2))));
    }


    /**
     * Configure WireMock to handle creating resources.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpResourceCreateDelete()
            throws IOException {
        InputStream response = getClass().getClassLoader().getResourceAsStream("resources/response.rdf");
        stubFor(post(urlEqualTo("/ro1/")).withHeader("Slug", equalTo("res1.txt")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "text/plain")
                    .withHeader("Location", MOCK_RESOURCE_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/ro1/res1.txt")).willReturn(aResponse().withStatus(204)));
    }

}
