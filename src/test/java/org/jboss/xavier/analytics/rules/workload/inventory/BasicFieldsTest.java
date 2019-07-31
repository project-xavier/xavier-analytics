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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicFieldsTest extends BaseTest {

    public BasicFieldsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/BasicFields.drl", ResourceType.DRL);
    }

    @Test
    public void test() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 1);

        Map<String, Object> facts = new HashMap<>();

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(new Long(100000001));
        vmWorkloadInventoryModel.setMemory(new Long(4096));
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        vmWorkloadInventoryModel.setOsProductName("RHEL");
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Copy basic fields and agenda controller");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<WorkloadInventoryReportModel> reports = objects.stream()
                .filter(object -> object instanceof WorkloadInventoryReportModel)
                .map(object -> (WorkloadInventoryReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals("IMS vCenter",report.getProvider());
        Assert.assertEquals("V2V-DC",report.getDatacenter());
        Assert.assertEquals("Cluster 1",report.getCluster());
        Assert.assertEquals("vm tests",report.getVmName());
        Assert.assertEquals(new BigDecimal(100000001).intValue(),report.getDiskSpace().intValue());
        Assert.assertEquals(4096,report.getMemory().intValue());
        Assert.assertEquals(4,report.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",report.getOsDescription());
        Assert.assertEquals("RHEL",report.getOsName());

    }
}
