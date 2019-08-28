package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.functions.HelperFunctions;
import org.jboss.xavier.analytics.pojo.input.workload.inventory.VMWorkloadInventoryModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TargetsTest extends BaseTest {

    public TargetsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/Targets.drl", ResourceType.DRL);
    }

    private void checkLoadedRulesNumber()
    {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 3);
    }

    @Test
    public void testTargetsFlagsExistSupportedOS() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(1, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
    }

    @Test
    public void testTargetsFlagsExistUnSupportedOS() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Debian");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(1, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
    }

    @Test
    public void testTargetsNoFlagsSupportedOS() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(2, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("OSP".toLowerCase())));
    }

    @Test
    public void testTargetsNoFlagsUnSupportedOS() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Debian");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNotNull(report.getRecommendedTargetsIMS());
        Assert.assertEquals(2, report.getRecommendedTargetsIMS().size());
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("RHV".toLowerCase())));
        Assert.assertTrue(report.getRecommendedTargetsIMS().stream().anyMatch(target -> target.toLowerCase().contains("OSP".toLowerCase())));
    }

    @Test
    public void testTargetsCentOS() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CentOS");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_Convert2RHEL");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

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
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Targets");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Oracle Enterprise Linux Server");

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Target_RHV", "Target_OSP","Target_Convert2RHEL");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

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
