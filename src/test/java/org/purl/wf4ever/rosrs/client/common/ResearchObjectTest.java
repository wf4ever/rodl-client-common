package org.purl.wf4ever.rosrs.client.common;

import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

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
    public final void testCreate() {
        fail("Not yet implemented");
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
        Assert.assertEquals(URI.create("http://example.org/"), ro1.getRosrs().getRodlURI());
    }


    /**
     * Test that the example RO says it's loaded.
     */
    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(ro1.isLoaded());
    }


    @Test
    public final void testLoad() {
        fail("Not yet implemented");
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


    @Test
    public final void testGetCreated() {
        fail("Not yet implemented");
    }

}
