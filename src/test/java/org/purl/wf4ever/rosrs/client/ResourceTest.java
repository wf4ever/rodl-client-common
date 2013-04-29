package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.ORE;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test {@link Resource} methods.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Resource that will be mapped to local resources. */
    private static final URI RES_URI = URI.create("http://example.org/ro1/res1.txt");

    /** Proxy of the resource that will be mapped to local resources. */
    private static final URI PROXY_URI = URI.create("http://example.org/ro1/proxies/1");

    /** Some RO available by HTTP. */
    private static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some resource available by HTTP. */
    private static final URI MOCK_RESOURCE = URI.create("http://localhost:8089/res1.txt");

    /** Some resource available by HTTP. */
    private static final URI MOCK_RESOURCE_PROXY = URI.create("http://localhost:8089/resproxy");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A test resource. */
    private static Resource res1;


    /**
     * Prepare a loaded resource.
     * 
     * @throws Exception
     *             when the test data can't be loaded
     */
    @BeforeClass
    public static final void setUpBeforeClass()
            throws Exception {
        rosrs = new ROSRService(URI.create("http://localhost:8089/"), null);
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
        res1 = new Resource(ro1, RES_URI, PROXY_URI, URI.create("http://test1.myopenid.com"), new DateTime(2011, 12,
                02, 15, 02, 10, DateTimeZone.UTC));
    }


    @Before
    public void setUp()
            throws Exception {
    }


    /**
     * Test a resource can be created.
     */
    @Test
    public final void testResource() {
        Resource res = new Resource(ro1, RES_URI, PROXY_URI, URI.create("http://test1.myopenid.com"), new DateTime(
                2011, 12, 02, 15, 02, 10, DateTimeZone.UTC));
        Assert.assertNotNull(res);
    }


    /**
     * Test aggregating an internal resource.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     * @throws IOException
     *             can't open the test file
     */
    @Test
    public final void testCreateResearchObjectStringInputStreamString()
            throws ROSRSException, IOException {
        // this is what the mock HTTP server will return
        InputStream response = getClass().getClassLoader().getResourceAsStream("resources/response.rdf");
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "text/plain")
                    .withHeader("Location", MOCK_RESOURCE_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/res1.txt")).willReturn(aResponse().withStatus(204)));

        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("resources/res1.txt")) {
            Resource res = Resource.create(ro, "res1.txt", in, "text/plain");
            Assert.assertNotNull(res);
            res.delete();
        }
        verify(postRequestedFor(urlMatching("/ro1/")).withHeader("Content-Type", equalTo("text/plain")));
        ro.delete();
    }


    /**
     * Test creating an external resource.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     * @throws IOException
     *             can't open the test file
     */
    @Test
    public final void testCreateResearchObjectURI()
            throws ROSRSException, IOException {
        // this is what the mock HTTP server will return
        InputStream response = getClass().getClassLoader().getResourceAsStream("resources/response_external.rdf");
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RESOURCE_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/res1.txt")).willReturn(aResponse().withStatus(204)));

        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        Resource res = Resource.create(ro, URI.create("http://example.org/externalresource"));
        Assert.assertNotNull(res);
        verify(postRequestedFor(urlMatching("/ro1/")).withHeader("Content-Type",
            equalTo("application/vnd.wf4ever.proxy")));
        res.delete();
        ro.delete();
    }


    /**
     * Test we can get annotations about the resource.
     */
    @Test
    public final void testGetAnnotations() {
        Set<Annotation> ex = new HashSet<>();
        ex.add(new Annotation(ro1, RO_PREFIX.resolve(".ro/annotations/2"), URI
                .create("http://example.org/externalbody1.rdf"), RES_URI, URI.create("http://test.myopenid.com"),
                new DateTime(2012, 12, 11, 12, 06, 53, 551, DateTimeZone.UTC)));
        Set<Annotation> res = new HashSet<>();
        res.addAll(res1.getAnnotations());
        Assert.assertEquals(ex, res);
    }


    /**
     * Test we can get the URI.
     */
    @Test
    public final void testGetUri() {
        Assert.assertEquals(RES_URI, res1.getUri());
    }


    /**
     * Test we can get the RO.
     */
    @Test
    public final void testGetResearchObject() {
        Assert.assertEquals(ro1, res1.getResearchObject());
    }


    /**
     * Test we can get the proxy.
     */
    @Test
    public final void testGetProxyUri() {
        Assert.assertEquals(PROXY_URI, res1.getProxyUri());
    }


    /**
     * Test we can get the creator.
     */
    @Test
    public final void testGetCreator() {
        Assert.assertEquals(URI.create("http://test1.myopenid.com"), res1.getCreator());
    }


    /**
     * Test we can get the creation date.
     */
    @Test
    public final void testGetCreated() {
        Assert.assertEquals(new DateTime(2011, 12, 02, 15, 02, 10, DateTimeZone.UTC), res1.getCreated());
    }

}
