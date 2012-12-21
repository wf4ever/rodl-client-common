package org.purl.wf4ever.rosrs.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test {@link Resource} methods.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTest {

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Resource that will be mapped to local resources. */
    private static final URI RES_URI = URI.create("http://example.org/ro1/res1.txt");

    /** Proxy of the resource that will be mapped to local resources. */
    private static final URI PROXY_URI = URI.create("http://example.org/ro1/proxies/1");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A test resource. */
    private static Resource res1;


    /**
     * Prepare a loaded resource.
     * 
     * @throws ROException
     *             example RO has incorrect data
     * @throws ROSRSException
     *             could not load the example RO
     * @throws IOException
     *             could not load the example annotation
     */
    @BeforeClass
    public static final void setUp()
            throws ROSRSException, ROException, IOException {
        ROSRService rosrs = new ROSRService(URI.create("http://example.org/"), "foo");
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
        res1 = new Resource(ro1, RES_URI, PROXY_URI, URI.create("http://test1.myopenid.com"), new DateTime(2011, 12,
                02, 15, 02, 10, DateTimeZone.UTC));
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
        ResearchObject ro;
        try {
            ro = ResearchObject.create(TestUtils.ROSRS, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(TestUtils.ROSRS.getRosrsURI().resolve("JavaClientTest/"), TestUtils.ROSRS);
                ro.delete();
                ro = ResearchObject.create(TestUtils.ROSRS, "JavaClientTest");
            } else {
                throw e;
            }
        }
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/res1.txt")) {
            Resource res = Resource.create(ro, "res1.txt", in, "text/plain");
            Assert.assertNotNull(res);
            res.delete();
        }
        ro.delete();
    }


    /**
     * Test creating an external resource.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     */
    @Test
    public final void testCreateResearchObjectURI()
            throws ROSRSException {
        ResearchObject ro;
        try {
            ro = ResearchObject.create(TestUtils.ROSRS, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(TestUtils.ROSRS.getRosrsURI().resolve("JavaClientTest/"), TestUtils.ROSRS);
                ro.delete();
                ro = ResearchObject.create(TestUtils.ROSRS, "JavaClientTest");
            } else {
                throw e;
            }
        }
        Resource res = Resource.create(ro, URI.create("http://example.org/externalresource"));
        Assert.assertNotNull(res);
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
