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
                {"SUSE Linux Enterprise 12 (64-bit)", new ArrayList<>(Arrays.asList("No_Flag_Supported_OS", "Category_Suitable")), null, WorkloadInventoryReportModel.COMPLEXITY_EASY, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OSP)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OSP)), WorkloadInventoryReportModel.FLAG_CATEGORY_SUITABLE},
                {"Oracle Linux 6 (64-bit)", new ArrayList<>(Arrays.asList("No_Flag_Convertible_OS", "Category_Suitable")), null, WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OSP, WorkloadInventoryReportModel.TARGET_RHEL)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OSP, WorkloadInventoryReportModel.TARGET_RHEL )), WorkloadInventoryReportModel.FLAG_CATEGORY_SUITABLE},
                // this VM tests specifically that a VM suitable for OCP, once added the "Shared Disk" flag, got the WorkloadInventoryReportModel.TARGET_OCP target removed (ie triggering "Target_OCP_Reevaluate" rule)
                {"Red Hat Enterprise Linux 5 (64-bit)", new ArrayList<>(Arrays.asList("Target_OCP_Reevaluate", "One_Flag_Supported_OS", "Category_Critical")), Collections.singleton(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME), WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OCP)), WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL},
                {"CentOS 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Or_More_Flags_Convertible_OS", "Category_Critical")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_HARD, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL},
                // this VM tests specifically that a VM suitable for OCP, without the "Shared Disk" flag, still has the WorkloadInventoryReportModel.TARGET_OCP target (ie NOT triggering "Target_OCP_Reevaluate" rule)
                {"Microsoft Windows 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Flag_Supported_OS", "Category_Suitable")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_MEDIUM, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OCP)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_OCP)), WorkloadInventoryReportModel.FLAG_CATEGORY_SUITABLE},
                {"Microsoft Windows XP Professional (32-bit)", new ArrayList<>(Arrays.asList("Target_None_Reevaluate", "No_Flags_Not_Supported_OS", "Category_Suitable")), null, WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED, Collections.singleton("None"), null, WorkloadInventoryReportModel.FLAG_CATEGORY_SUITABLE},
                {"Ubuntu", new ArrayList<>(Arrays.asList("Target_None_Reevaluate", "One_Or_More_Flags_Not_Supported_OS", "Category_Suitable")), Collections.singleton(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME), WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED, Collections.singleton("None"), null, WorkloadInventoryReportModel.FLAG_CATEGORY_SUITABLE},
                {"", new ArrayList<>(Arrays.asList("Target_None_Reevaluate", "Not_Detected_OS", "Category_Critical")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN, Collections.singleton("None"), null, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL},
                {"Not detected", new ArrayList<>(Arrays.asList("Target_None_Reevaluate", "Not_Detected_OS", "Category_Critical")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME, WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN, Collections.singleton("None"), null, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL},
                // check information and warning categories
                {"CentOS 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Or_More_Flags_Convertible_OS", "Category_Warning")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.USB_CONTROLLERS_FLAG_NAME, WorkloadInventoryReportModel.VM_DRS_CONFIG_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_HARD, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING},
                {"CentOS 7 (64-bit)", new ArrayList<>(Arrays.asList("One_Or_More_Flags_Convertible_OS", "Category_Information")), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.VM_DRS_CONFIG_FLAG_NAME, WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME)), WorkloadInventoryReportModel.COMPLEXITY_HARD, new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), new HashSet<>(Arrays.asList(WorkloadInventoryReportModel.TARGET_RHV, WorkloadInventoryReportModel.TARGET_RHEL)), WorkloadInventoryReportModel.FLAG_CATEGORY_INFORMATION}
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

    @Parameterized.Parameter(6)
    public String categoryExpected;

    public WorkloadInventoryReevaluateTest()
    {
        super("WorkloadInventoryReevaluateKSession0", "org.jboss.xavier.analytics.rules.*", 16);
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
        // Category
        assertEquals(categoryExpected, workloadInventoryReportModel.getVmCategory());
    }
}
