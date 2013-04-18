package org.purl.wf4ever.rosrs.client.evo;

import org.junit.Assert;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.BaseTest;
import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class ROEVOServiceTest extends BaseTest {

    /**
     * Create a new snapshot job, verify the correct response.
     */
    @Test
    public final void testCreateSnapshotNoFinalize() {
        JobStatus status = roevo.createSnapshot(ro.getUri(), "TestSnapshot", false);
        Assert.assertEquals(JobStatus.State.RUNNING, status.getState());
        Assert.assertEquals(ro.getUri(), status.getCopyfrom());
        Assert.assertNotNull(status.getTarget());
        rosToDelete.add(new ResearchObject(ro.getUri().resolve(status.getTarget()), rosrs));
    }


    /**
     * Get a job status, verify it has correct data.
     */
    @Test
    public final void testGetJobStatus() {
        JobStatus status = roevo.createSnapshot(ro.getUri(), "TestSnapshot", false);
        Assert.assertEquals(JobStatus.State.RUNNING, status.getState());
        Assert.assertEquals(ro.getUri(), status.getCopyfrom());
        Assert.assertNotNull(status.getTarget());
        rosToDelete.add(new ResearchObject(ro.getUri().resolve(status.getTarget()), rosrs));

        JobStatus status2 = roevo.getStatus(status.getUri());
        Assert.assertEquals(status.getTarget(), status2.getTarget());
        Assert.assertEquals(status.getCopyfrom(), status2.getCopyfrom());
        Assert.assertEquals(status.getUri(), status2.getUri());
        Assert.assertEquals(status.isFinalize(), status2.isFinalize());
    }

}
