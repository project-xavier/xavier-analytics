package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.pojo.input.workload.inventory.VMWorkloadInventoryModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicFieldsTest extends BaseTest {

    public BasicFieldsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/BasicFields.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory", 1);
    }

    @Test
    public void testWithAllInputFields() throws ParseException {
        Map<String, Object> facts = new HashMap<>();

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        vmWorkloadInventoryModel.setOsProductName("RHEL");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");
        vmWorkloadInventoryModel.setScanRunDate(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"));
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Copy basic fields and agenda controller");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

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
        Assert.assertEquals("VMware vCenter", report.getProduct());
        Assert.assertEquals("6.5", report.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", report.getHost_name());
        Assert.assertEquals(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"), report.getCreationDate());

    }

    @Test
    public void testWithoutOptionalInputFields() throws ParseException {
        Map<String, Object> facts = new HashMap<>();

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        vmWorkloadInventoryModel.setOsProductName("RHEL");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setScanRunDate(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"));
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Copy basic fields and agenda controller");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertEquals("IMS vCenter",report.getProvider());
        Assert.assertNull(report.getDatacenter());
        Assert.assertNull(report.getCluster());
        Assert.assertEquals("vm tests",report.getVmName());
        Assert.assertEquals(new BigDecimal(100000001).intValue(),report.getDiskSpace().intValue());
        Assert.assertEquals(4096,report.getMemory().intValue());
        Assert.assertEquals(4,report.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",report.getOsDescription());
        Assert.assertEquals("RHEL",report.getOsName());
        Assert.assertEquals("VMware vCenter", report.getProduct());
        Assert.assertEquals("6.5", report.getVersion());
        Assert.assertNull(report.getHost_name());
        Assert.assertEquals(new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss.S").parse("2019-09-18T14:52:45.871Z"), report.getCreationDate());
    }
}
