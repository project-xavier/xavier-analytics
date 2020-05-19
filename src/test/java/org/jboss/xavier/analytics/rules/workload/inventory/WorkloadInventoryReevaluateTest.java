package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseIntegrationTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class WorkloadInventoryReevaluateTest extends BaseIntegrationTest {

    @Parameterized.Parameters(name = "{index}: Test OS name {0} for rule {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"SUSE Linux Enterprise 12 (64-bit)", new ArrayList<>(Arrays.asList("No_Flag_Supported_OS")), null, WorkloadInventoryReportModel.COMPLEXITY_EASY, new HashSet<>(Arrays.asList("RHV", "OSP")), new HashSet<>(Arrays.asList("RHV", "OSP"))},
                {"Oracle Linux 6 (64-bit)", new ArrayList<>(Arrays.asList("No_Flag_Convertible_OS")), null, WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList("RHV", "OSP", "RHEL")), new HashSet<>(Arrays.asList("RHV", "OSP", "RHEL"))},
                // this VM tests specifically that a VM suitable for OCP, once added the "Shared Disk" flag, got the "OCP" target removed (ie triggering "Target_OCP_Reevaluate" rule)
                {"Red Hat Enterprise Linux 5 (64-bit)", new ArrayList<>(Arrays.asList("Target_OCP_Reevaluate", "One_Flag_Supported_OS")), Collections.singleton(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME), WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList("RHV")), new HashSet<>(Arrays.asList("RHV", "OCP"))},
                {"CentOS 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Or_More_Flags_Convertible_OS")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_HARD, new HashSet<>(Arrays.asList("RHV", "RHEL")), new HashSet<>(Arrays.asList("RHV", "RHEL"))},
                // this VM tests specifically that a VM suitable for OCP, without the "Shared Disk" flag, still has the "OCP" target (ie NOT triggering "Target_OCP_Reevaluate" rule)
                {"Microsoft Windows 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Flag_Supported_OS")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList("RHV", "OCP")), new HashSet<>(Arrays.asList("RHV", "OCP"))},
                {"Microsoft Windows XP Professional (32-bit)", new ArrayList<>(Arrays.asList("No_Flags_Not_Supported_OS")), null, WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED, Collections.singleton("None"), Collections.singleton("None")},
                {"Ubuntu", new ArrayList<>(Arrays.asList("One_Or_More_Flags_Not_Supported_OS")), Collections.singleton(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME), WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED, Collections.singleton("None"), Collections.singleton("None")},
                {"", new ArrayList<>(Arrays.asList("Not_Detected_OS")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN, Collections.singleton("None"), Collections.singleton("None")},
                {"Not detected", new ArrayList<>(Arrays.asList("Not_Detected_OS")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN, Collections.singleton("None"), Collections.singleton("None")},
        });
    }

    @Parameterized.Parameter
    public String os;

    @Parameterized.Parameter(1)
    public List<String> ruleNamesExpected;

    @Parameterized.Parameter(2)
    public Set<String> flags;

    @Parameterized.Parameter(3)
    public String complexityExpected;

    @Parameterized.Parameter(4)
    public Set<String> targetsExpected;

    @Parameterized.Parameter(5)
    public Set<String> targetsInitial;

    public WorkloadInventoryReevaluateTest()
    {
        super("WorkloadInventoryReevaluateKSession0", "org.jboss.xavier.analytics.rules.*", 11);
    }

    private static WorkloadInventoryReportModel generateWorkloadInventoryReportModel() throws ParseException
    {
        //Basic Fields
        final WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setProvider("IMS vCenter");
        workloadInventoryReportModel.setDatacenter("V2V-DC");
        workloadInventoryReportModel.setCluster("Cluster 1");
        workloadInventoryReportModel.setVmName("vm tests");
        workloadInventoryReportModel.setDiskSpace(100000001L);
        workloadInventoryReportModel.setMemory(4096L);
        workloadInventoryReportModel.setCpuCores(4);
        workloadInventoryReportModel.setProduct("VMware vCenter");
        workloadInventoryReportModel.setVersion("6.5");
        workloadInventoryReportModel.setHost_name("esx13.v2v.bos.redhat.com");
        workloadInventoryReportModel.setCreationDate(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"));
        workloadInventoryReportModel.setComplexity(WorkloadInventoryReportModel.COMPLEXITY_EASY);
        workloadInventoryReportModel.setSsaEnabled(true);
        return  workloadInventoryReportModel;
    }

    private static void checkUnchangedFields(WorkloadInventoryReportModel workloadInventoryReportModel) throws ParseException
    {
        // Check that the object has exactly the fields that the rule tested should NOT add/change
        // BasicFields
        assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        assertEquals("6.5", workloadInventoryReportModel.getVersion());
        assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        assertEquals(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"), workloadInventoryReportModel.getCreationDate());
        // Workloads
        Assert.assertTrue(workloadInventoryReportModel.getSsaEnabled());
    }

    // Method with checks specific to this test cases: # of rules fired, agenda group focus rules name
    private WorkloadInventoryReportModel checkResultAndGet(Map<String, Object> results)
    {
        // check that the number of rules fired is what you expect
        ruleNamesExpected.add(0, "AgendaFocusForReevaluate");
        assertEquals(ruleNamesExpected.size(), results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener, ruleNamesExpected.toArray(new String[0]));
        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);
        // Check that the number of object is the right one (in this case, there must be just one report)
        assertEquals(1, queryResults.size());
        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));
        return (WorkloadInventoryReportModel) queryResultsRow.get("report");
    }

    @Test
    public void test() throws ParseException
    {
        WorkloadInventoryReportModel inputWorkloadInventoryReportModel = generateWorkloadInventoryReportModel();
        inputWorkloadInventoryReportModel.setOsDescription(os);
        inputWorkloadInventoryReportModel.setFlagsIMS(flags);
        inputWorkloadInventoryReportModel.setRecommendedTargetsIMS(targetsInitial);

        Map<String, Object> facts = new HashMap<>();
        facts.put("workloadInventoryReportModel", inputWorkloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts, "GetWorkloadInventoryReports");
        WorkloadInventoryReportModel workloadInventoryReportModel = checkResultAndGet(results);
        checkUnchangedFields(workloadInventoryReportModel);
        // BasicFields
        assertEquals(os, workloadInventoryReportModel.getOsDescription());
        // Flags
        final Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        if (flags != null)
        {
            assertNotNull(flagsIMS);
            assertEquals(flags.size(), flagsIMS.size());
            assertTrue(flagsIMS.containsAll(flags));
        }
        // Complexity
        assertEquals(complexityExpected, workloadInventoryReportModel.getComplexity());
        // Targets
        final Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        assertNotNull(targets);
        assertEquals(targetsExpected.size(), targets.size());
        assertArrayEquals(targetsExpected.toArray(new String[0]), targets.toArray(new String[0]));
    }
}
