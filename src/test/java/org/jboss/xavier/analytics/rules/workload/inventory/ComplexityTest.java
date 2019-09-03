package org.jboss.xavier.analytics.rules.workload.inventory;

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

public class ComplexityTest extends BaseTest {

    public ComplexityTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/Complexity.drl", ResourceType.DRL);
    }

    @Test
    public void testNoFlagSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Red Hat Enterprise Linux v7.6");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "No_Flag_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,report.getComplexity());

    }

    @Test
    public void testOneFlagSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("SUSE Linux Enterprise Server");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Flag_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,report.getComplexity());

    }

    @Test
    public void testMoreThanOneFlagSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Microsoft Windows");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "More_Than_One_Flag_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }

    @Test
    public void testNoFlagsUnSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("Ubuntu");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "No_Flags_Not_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,report.getComplexity());

    }

    @Test
    public void testOneFlagUnSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("CENTOS");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Not_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }

    @Test
    public void testOneOrMoreFlagsUnSupportedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("DEBIAN");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "One_Or_More_Flags_Not_Supported_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,report.getComplexity());

    }

    @Test
    public void testNoFlagUndetectedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("OSX");
        workloadInventoryReportModel.setFlagsIMS(null);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }

    @Test
    public void testOneFlagUndetectedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("OSX");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }



    @Test
    public void testMoreThanOneFlagUndetectedOS() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 6);

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Complexity");

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setOsDescription("OSX");
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME);
        workloadInventoryReportModel.addFlagIMS(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME);

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Not_Detected_OS");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,report.getComplexity());

    }


}

