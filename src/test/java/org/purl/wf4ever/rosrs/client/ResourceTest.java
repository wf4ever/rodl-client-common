package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.notMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROException;
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

    /** A test resource. */
    private Resource res1;


    /**
     * Set up a sample RO.
     * 
     * @throws Exception
     *             if there are any problem with test resources
     */
    @Before
    public void setUp()
            throws Exception {
        super.setUp();
        res1 = new Resource(ro1, MOCK_RESOURCE, MOCK_RESOURCE_PROXY, PERSON_1, new DateTime(2011, 12, 02, 15, 02, 10,
                DateTimeZone.UTC));
    }


    /**
     * Configure WireMock to handle adding an external resource.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpExternalResourceCreateDelete()
            throws IOException {
        InputStream response = getClass().getClassLoader().getResourceAsStream("resources/response_external.rdf");
        stubFor(post(urlEqualTo("/ro1/")).withHeader("Content-Type", equalTo("application/vnd.wf4ever.proxy"))
                .willReturn(
                    aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                            .withHeader("Location", MOCK_EXT_RESOURCE_PROXY.toString())
                            .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/extresproxy")).willReturn(aResponse().withStatus(204)));
    }


    /**
     * Test a resource can be created.
     */
    @Test
    public final void testResource() {
        Resource res = new Resource(ro1, MOCK_RESOURCE, MOCK_RESOURCE_PROXY, PERSON_1, new DateTime(2011, 12, 02, 15,
                02, 10, DateTimeZone.UTC));
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
        setUpExternalResourceCreateDelete();
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
        Annotation an1 = new Annotation(ro1, MOCK_RO.resolve(".ro/annotations/2"),
                URI.create("http://example.org/externalbody1.rdf"), Collections.singleton(MOCK_RESOURCE), PERSON,
                new DateTime(2012, 12, 11, 12, 06, 53, 551, DateTimeZone.UTC));
        Set<URI> targets = new HashSet<>();
        targets.add(MOCK_RO);
        targets.add(MOCK_RESOURCE);
        Annotation an2 = new Annotation(ro1, MOCK_RO.resolve(".ro/annotations/1"), MOCK_RO.resolve("body.rdf"),
                targets, PERSON, new DateTime(2012, 12, 11, 12, 06, 53, 551, DateTimeZone.UTC));
        Collection<Annotation> annotations = res1.getAnnotations();
        assertThat(annotations, hasSize(equalTo(2)));
        assertThat(annotations, hasItem(an1));
        assertThat(annotations, hasItem(an2));
    }


    /**
     * Test we can get the URI.
     */
    @Test
    public final void testGetUri() {
        Assert.assertEquals(MOCK_RESOURCE, res1.getUri());
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
        Assert.assertEquals(MOCK_RESOURCE_PROXY, res1.getProxyUri());
    }


    /**
     * Test we can get the creator.
     */
    @Test
    public final void testGetAuthor() {
        Assert.assertEquals(PERSON_1, res1.getAuthor());
    }


    /**
     * Test we can get the creation date.
     */
    @Test
    public final void testGetCreated() {
        Assert.assertEquals(new DateTime(2011, 12, 02, 15, 02, 10, DateTimeZone.UTC), res1.getCreated());
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     */
    @Test
    public final void shouldReturnTwoCommentsJoined()
            throws ROSRSException {
        Map<Annotation, String> map = res1.getPropertyValues(RDFS_COMMENT);
        assertThat(map.values(), hasSize(equalTo(1)));
        assertThat(map, anyOf(hasValue("Res1 comment 1; Res1 comment 2"), hasValue("Res1 comment 2; Res1 comment 1")));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     */
    @Test
    public final void shouldUpdateAComment()
            throws ROSRSException {
        Map<Annotation, String> map = res1.getPropertyValues(RDFS_COMMENT);
        assertThat(map.values(), hasSize(equalTo(1)));
        Entry<Annotation, String> e = map.entrySet().iterator().next();
        res1.updatePropertyValue(e.getKey(), RDFS_COMMENT, "Res1 comment 3");

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(matching(".*Res1 comment 3.*")));

        map = res1.getPropertyValues(RDFS_COMMENT);
        assertThat(map.values(), hasSize(equalTo(1)));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     * @throws ROException
     *             incorrect manifest
     */
    @Test
    public final void shouldCreateAComment()
            throws ROSRSException, ROException {
        Map<Annotation, String> map = res1.getPropertyValues(RDFS_COMMENT);
        assertThat(map.values(), hasSize(equalTo(1)));
        Annotation annotation = res1.createPropertyValue(RDFS_COMMENT, "Res1 comment 3");
        assertThat(annotation, notNullValue());

        verify(postRequestedFor(urlEqualTo("/ro1/")).withRequestBody(matching(".*Res1 comment 3.*")));
    }


    /**
     * See name.
     * 
     * @throws ROSRSException
     *             wiremock error
     * @throws ROException
     *             incorrect manifest
     */
    @Test
    public final void shouldDeleteAComment()
            throws ROSRSException, ROException {
        Map<Annotation, String> map = res1.getPropertyValues(RDFS_COMMENT);
        assertThat(map.values(), hasSize(equalTo(1)));
        Entry<Annotation, String> e = map.entrySet().iterator().next();
        res1.deletePropertyValue(e.getKey(), RDFS_COMMENT);

        verify(putRequestedFor(urlEqualTo("/ro1/body.rdf")).withRequestBody(notMatching(".*Res1 comment.*")));
    }

}
