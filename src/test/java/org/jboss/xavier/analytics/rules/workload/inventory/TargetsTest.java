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

public class TargetsTest extends BaseTest {

    public TargetsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/Targets.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory", 3);
    }

    @Test
    public void testTargetsFlagsExistSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(1, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
    }

    @Test
    public void testTargetsFlagsExistConvertibleOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_Convert2RHEL");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(2, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
    }

    @Test
    public void testTargetsNoFlagsSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(2, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("OSP".toLowerCase())));
    }

    @Test
    public void testTargetsNoFlagsConvertibleOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP", "Target_Convert2RHEL");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(3, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("OSP".toLowerCase())));
    }

    @Test
    public void testTargetsCentOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_Convert2RHEL");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(2, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("Convert2RHEL".toLowerCase())));
    }

    @Test
    public void testTargetsOracle() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Oracle Enterprise Linux Server");

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP","Target_Convert2RHEL");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(3, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("OSP".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("Convert2RHEL".toLowerCase())));
    }
}
