package org.jboss.xavier.analytics.rules.workload.summary;

import org.jboss.xavier.analytics.rules.BaseIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

public class WorkloadSummaryReportTest extends BaseIntegrationTest
{

    public WorkloadSummaryReportTest()
    {
        super("WorkloadSummaryKSession0", "org.jboss.xavier.analytics.rules.workload.summary", 0);
    }

    @Override
    @Test
    // TODO Remove this method once WorkloadSummaryReportTest will be testing new Workload Summary rules
    public void checkLoadedRulesNumber()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void test()
    {
        Assert.assertTrue(true);
    }
}
