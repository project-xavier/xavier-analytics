package org.jboss.xavier.analytics.rules.workload.inventory;

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
import java.util.Set;
import java.util.stream.Collectors;

public class WorkloadsTest extends BaseTest {

    public WorkloadsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/Workloads.drl", ResourceType.DRL);
    }

    private void checkLoadedRulesNumber()
    {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 2);
    }

    @Test
    public void test() {
        checkLoadedRulesNumber();

        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Workloads");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Workloads_sample_systemServicesNames_rule", "Workloads_sample_files_rule");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
    }

}

