package org.purl.wf4ever.rosrs.client.evo;

import org.junit.Assert;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.evo.JobStatus.State;

public class JobStatusTest extends BaseTest {

    @Test
    public final void testRefresh() {
        JobStatus status = ro.snapshot("TestSnapshot");
        rosToDelete.add(new ResearchObject(ro.getUri().resolve(status.getTarget()), rosrs));

        while (status.getState() == State.RUNNING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status.refresh();
        }
        Assert.assertEquals(State.DONE, status.getState());
    }
}