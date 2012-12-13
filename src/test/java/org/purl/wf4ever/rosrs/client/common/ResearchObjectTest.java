package org.purl.wf4ever.rosrs.client.common;

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
 * Test the ResearchObject class.
 * 
 * @author piotrekhol
 * 
 */
public class ResearchObjectTest {

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Some ROSRS available by HTTP. */
    private static final ROSRService ROSRS = new ROSRService(
            URI.create("http://sandbox.wf4ever-project.org/rodl/ROs/"), "32801fc0-1df1-4e34-b");

    /** Some RO available by HTTP. */
    private static final URI PUBLIC_RO = URI.create("http://sandbox.wf4ever-project.org/rodl/ROs/AstronomyPack/");

    /** A loaded RO. */
    private static ResearchObject ro1;


    /**
     * Prepare a loaded RO.
     * 
     * @throws ROException
     *             example RO has incorrect data
     * @throws ROSRSException
     *             could not load the example RO
     */
    @BeforeClass
    public static final void setUp()
            throws ROSRSException, ROException {
        ROSRService rosrs = new ROSRService(URI.create("http://example.org/"), "foo");
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
    }


    /**
     * Test that an initial RO is not loaded.
     */
    @Test
    public final void testResearchObject() {
        ResearchObject ro = new ResearchObject(RO_PREFIX, null);
        Assert.assertFalse(ro.isLoaded());
    }


    @Test
    public final void testCreateDelete()
            throws ROSRSException {
        ResearchObject ro;
        try {
            ro = ResearchObject.create(ROSRS, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(ROSRS.getRosrsURI().resolve("JavaClientTest/"), ROSRS);
                ro.delete();
                ro = ResearchObject.create(ROSRS, "JavaClientTest");
            } else {
                throw e;
            }
        }
        ro.delete();
    }


    /**
     * Test the example RO URI.
     */
    @Test
    public final void testGetUri() {
        Assert.assertEquals(RO_PREFIX, ro1.getUri());
    }


    /**
     * Test the ROSRS client is saved.
     */
    @Test
    public final void testGetRosrs() {
        Assert.assertNotNull(ro1.getRosrs());
        Assert.assertEquals(URI.create("http://example.org/"), ro1.getRosrs().getRosrsURI());
    }


    /**
     * Test that the example RO says it's loaded.
     */
    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(ro1.isLoaded());
    }


    /**
     * Test that an RO can be loaded over HTTP.
     * 
     * @throws ROSRSException
     *             connection problem
     * @throws ROException
     *             remote manifest is incorrect
     */
    @Test
    public final void testLoad()
            throws ROSRSException, ROException {
        ResearchObject ro = new ResearchObject(PUBLIC_RO, ROSRS);
        Assert.assertFalse(ro.isLoaded());
        ro.load();
        Assert.assertTrue(ro.isLoaded());
    }


    /**
     * Test ro:Resources identified.
     */
    @Test
    public final void testGetResources() {
        Set<Resource> res = new HashSet<>();
        res.add(new Resource(ro1, RO_PREFIX.resolve("res1"), RO_PREFIX.resolve("proxies/proxy1")));
        res.add(new Resource(ro1, RO_PREFIX.resolve("res2"), RO_PREFIX.resolve("proxies/proxy2")));
        Assert.assertEquals(res, ro1.getResources());
    }


    /**
     * Test ro:Folders identified.
     */
    @Test
    public final void testGetFolders() {
        Set<Folder> folders = new HashSet<>();
        folders.add(new Folder(ro1, RO_PREFIX.resolve("folder1"), RO_PREFIX.resolve("proxies/proxy3"), RO_PREFIX
                .resolve("folder1.ttl")));
        Assert.assertEquals(folders, ro1.getFolders());
    }


    /**
     * Test dcterms:creator.
     */
    @Test
    public final void testCreator() {
        Assert.assertEquals(URI.create("http://test.myopenid.com"), ro1.getCreator());
    }


    /**
     * Test RO dcterms:created.
     */
    @Test
    public final void testGetCreated() {
        //2011-12-02T16:01:10Z
        Assert.assertEquals(new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC), ro1.getCreated());
    }
}
