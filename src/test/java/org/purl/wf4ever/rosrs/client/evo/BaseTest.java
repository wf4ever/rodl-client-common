package org.purl.wf4ever.rosrs.client.evo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

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

    /** RODL access token. */
    private static final String TOKEN = "1cec3d40-4c6c-4bb8-8527-cbd8776c6327";

    /** RODL URI for testing. */
    protected static final URI RODL_URI = URI.create("http://sandbox.wf4ever-project.org/rodl/");

    /** ROs to delete after a test. */
    protected static List<ResearchObject> rosToDelete = new ArrayList<>();

    /** The Live RO. */
    protected ResearchObject ro;


    /**
     * Prepare an ROEVO service.
     * 
     * @throws Exception
     *             example RO has incorrect data
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception {
        rosrs = new ROSRService(RODL_URI.resolve("ROs/"), TOKEN);
        roevo = new ROEVOService(RODL_URI, TOKEN);
    }


    /**
     * Create the Live RO.
     * 
     * @throws Exception
     *             unexpected response from RODL
     */
    @Before
    public void setUp()
            throws Exception {
        ro = new ResearchObject(rosrs.getRosrsURI().resolve("ROEVOServiceTest/"), rosrs);
        ro.delete();
        ro = ResearchObject.create(rosrs, "ROEVOServiceTest");
        rosToDelete.add(ro);
    }


    /**
     * Remove all ROs created in the tests.
     * 
     * @throws ROSRSException
     *             unexpected response from RODL
     */
    @After
    public void tearDown()
            throws ROSRSException {
        for (ResearchObject roToDelete : rosToDelete) {
            roToDelete.delete();
        }
    }

}
