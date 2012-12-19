package org.purl.wf4ever.rosrs.client.common;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the {@link} methods.
 * 
 * @author piotrekhol
 * 
 */
public class FolderTest {

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Resource that will be mapped to local resources. */
    private static final URI FOLDER_URI = URI.create("http://example.org/ro1/folder1");

    /** Proxy of the resource that will be mapped to local resources. */
    private static final URI PROXY_URI = URI.create("http://example.org/ro1/proxies/3");

    /** Resource map of the resource that will be mapped to local resources. */
    private static final URI RMAP_URI = URI.create("http://example.org/ro1/folder1.ttl");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A loaded folder. */
    private static Folder fol1;


    /**
     * Prepare a loaded folder.
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
        fol1 = new Folder(ro1, FOLDER_URI, PROXY_URI, RMAP_URI, URI.create("http://test3.myopenid.com"), new DateTime(
                2011, 12, 02, 15, 02, 12, DateTimeZone.UTC), true);
        fol1.load(false);
    }


    @Test
    public final void testDelete() {
        fail("Not yet implemented");
    }


    @Test
    public final void testFolder() {
        //        Folder folder = new Folder();
    }


    @Test
    public final void testCreateResearchObjectString() {
        fail("Not yet implemented");
    }


    @Test
    public final void testLoad() {
        fail("Not yet implemented");
    }


    @Test
    public final void testAddEntry() {
        fail("Not yet implemented");
    }


    @Test
    public final void testAddSubFolder() {
        fail("Not yet implemented");
    }


    @Test
    public final void testGetResourceMap() {
        fail("Not yet implemented");
    }


    @Test
    public final void testIsLoaded() {
        fail("Not yet implemented");
    }


    @Test
    public final void testIsRootFolder() {
        fail("Not yet implemented");
    }


    @Test
    public final void testGetFolderEntries() {
        fail("Not yet implemented");
    }

}
