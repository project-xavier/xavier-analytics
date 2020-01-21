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

public class ReasonableDefaultsTest extends BaseTest {

    public ReasonableDefaultsTest()
    {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/ReasonableDefaults.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory", 3);
    }

    @Test
    public void testDatacenterFieldNullValueShouldFireRule() {
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setCluster("cluster");
        workloadInventoryReportModel.setHost_name("host");

        Map<String, Object> facts = new HashMap<>();
        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Fill 'datacenter' field with reasonable default");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.DATACENTER_DEFAULT_VALUE, report.getDatacenter());
    }

    @Test
    public void testClusterFieldNullValueShouldFireRule() {
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setDatacenter("datacenter");
        workloadInventoryReportModel.setHost_name("host");

        Map<String, Object> facts = new HashMap<>();
        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Fill 'cluster' field with reasonable default");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.CLUSTER_DEFAULT_VALUE, report.getCluster());
    }

    @Test
    public void testHostNameFieldNullValueShouldFireRule() {
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setCluster("cluster");
        workloadInventoryReportModel.setDatacenter("datacenter");

        Map<String, Object> facts = new HashMap<>();
        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Fill 'host_name' field with reasonable default");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals(WorkloadInventoryReportModel.HOST_NAME_DEFAULT_VALUE, report.getHost_name());
    }

    @Test
    public void testFieldsValidValuesShouldNotFireRules() {
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        workloadInventoryReportModel.setDatacenter("whatever");
        workloadInventoryReportModel.setCluster("cluster");
        workloadInventoryReportModel.setHost_name("host");

        Map<String, Object> facts = new HashMap<>();
        facts.put("vmWorkloadInventoryModel", workloadInventoryReportModel);
        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(0, results.get(NUMBER_OF_FIRED_RULE_KEY));
    }
}
