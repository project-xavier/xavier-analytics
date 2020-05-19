package org.jboss.xavier.analytics.rules.workload.inventory.complexity;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexityTest extends BaseTest {

    public ComplexityTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/complexity/Complexity.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory.complexity", 8);
    }

    @Test
    public void testNoFlagSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "No_Flag_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,report.getComplexity());

    }

    @Test
    public void testOneFlagSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("SUSE Linux Enterprise Server");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Flag_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,report.getComplexity());

    }



    @Test
    public void testOneFlagSuseOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("SUSE");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Flag_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,report.getComplexity());

    }

    @Test
    public void testMoreThanOneFlagSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Microsoft Windows");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "More_Than_One_Flag_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }

    @Test
    public void testNoFlagsUnSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Ubuntu");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "No_Flags_Not_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED,report.getComplexity());

    }

    @Test
    public void testOneFlagUnSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("UBUNTU");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Not_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED,report.getComplexity());

    }

    @Test
    public void testOneOrMoreFlagsUnSupportedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("DEBIAN");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Not_Supported_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNSUPPORTED,report.getComplexity());

    }

    @Test
    public void testNoFlagUndetectedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription(null);
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }

    @Test
    public void testOneFlagUndetectedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription(null);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }

    @Test
    public void testMoreThanOneFlagUndetectedOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription(WorkloadInventoryReportModel.OS_NAME_DEFAULT_VALUE);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }

    @Test
    public void testNoFlagConvertibleOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "No_Flag_Convertible_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,report.getComplexity());

    }

    @Test
    public void testOneFlagConvertibleOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Oracle Linux");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Convertible_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }

    @Test
    public void testMoreThanOneFlagConvertibleOS() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Convertible_OS");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }
}

