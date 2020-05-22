package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSFamilyTest extends BaseTest {

    public OSFamilyTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/OSFamily.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory", 8);
    }

    @Test
    public void testOtherFamilyRuleShouldBeFired() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "OSFamily");


        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Unrecognized OSName");

        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(
                this.agendaEventListener,
                "AgendaFocusForTest",
                "Fill 'osFamily' field with 'Other'"
        );

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.OS_FAMILY_DEFAULT_VALUE, report.getOsFamily());
    }

    @Test
    public void testIgnoreCase() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "OSFamily");


        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("red hat ENTERPRISE linux");

        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(
                this.agendaEventListener,
                "AgendaFocusForTest",
                "RHEL_OSFamily"
        );

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals("RHEL", report.getOsFamily());
    }

    @Test
    public void testWindowsServerFamily() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "OSFamily");


        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Windows WHATEVER Server");

        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(
                this.agendaEventListener,
                "AgendaFocusForTest",
                "Windows_OSFamily"
        );

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals("Windows Server", report.getOsFamily());
    }

}
