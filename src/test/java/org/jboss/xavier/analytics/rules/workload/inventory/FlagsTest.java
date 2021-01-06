package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.pojo.input.workload.inventory.VMWorkloadInventoryModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlagsTest extends BaseTest {

    public FlagsTest() {
        super("/org/jboss/xavier/analytics/rules/workload/inventory/Flags.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.workload.inventory", 17);
    }

    @Test
    public void test_NicsSharedVmdkAndRdmDiskFlags() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setNicsCount(5);
        vmWorkloadInventoryModel.setHasSharedVmdk(true);
        vmWorkloadInventoryModel.setHasRdmDisk(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();

        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Rdm_Disk", "Flag_Shared_VMDK");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(2, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.SHARED_VMDK_FLAG_NAME));

    }

    @Test
    public void test_NoFlags() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);
    }

    @Test
    public void test_OnlyNicsFlag()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setNicsCount(5);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);
    }

    @Test
    public void test_OnlyRdmDiskFlag()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasRdmDisk(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Rdm_Disk");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertFalse(flagsIMS.contains(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME));
    }

    @Test
    public void test_RdmDiskSharedVmdkFalse()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasRdmDisk(false);
        vmWorkloadInventoryModel.setHasSharedVmdk(false);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1
                , results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);
    }

    @Test
    public void test_CpuHotAdd()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasCpuHotAdd(true);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest","Flag_Cpu_Memory_Hotplug_Cpu_Add");


        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME));
    }

    @Test
    public void test_MemoryHotAdd()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasMemoryHotAdd(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest","Flag_Cpu_Memory_Hotplug_Memory_Add");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME));
    }

    @Test
    public void test_CpuHotRemove()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasCpuHotRemove(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Cpu_Memory_Hotplug_Cpu_Remove");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME));

    }

    @Test
    public void test_CpuHotAdd_False()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasCpuHotAdd(false);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");


        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);
    }

    @Test
    public void test_MemoryHotAdd_False()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasMemoryHotAdd(false);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);
    }

    @Test
    public void test_CpuHotRemove_False()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasCpuHotRemove(false);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);

        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);

    }
  
    @Test
    public void test_CpuAffinityNotNull()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setCpuAffinity("0,2");
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Cpu_Affinity");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME));

    }

    @Test
    public void test_CpuAffinityNull()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setCpuAffinity(null);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);

    }

    @Test
    public void test_hasPassthroughDeviceTrue () {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel1 = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel1.setHasPassthroughDevice(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel1);        
        
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_PassthroughDevice");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());

        Assert.assertEquals(1, reports.get(0).getFlagsIMS().size());
        Assert.assertTrue(reports.get(0).getFlagsIMS().contains(WorkloadInventoryReportModel.PASSTHROUGH_DEVICE_FLAG_NAME));
        
    }
    
    @Test
    public void test_hasPassthroughDeviceFalse () {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel1 = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel1.setHasPassthroughDevice(false);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel1);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());

        Assert.assertNull(reports.get(0).getFlagsIMS());

    }

    @Test
    public void test_NumaNodeAffinityNotNull()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setNumaNodeAffinity("set");
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);
  
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Numa_Node_Affinity");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.NUMA_NODE_AFFINITY_FLAG_NAME));

    }

    @Test
    public void test_VmHostAffinityConfigured()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasVmAffinityConfig(true);
      
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_VM_Host_Affinity_Configured");
        
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.VM_HOST_AFFINITY_CONFIGURED_FLAG_NAME));

    }
  
    public void test_hasPassthroughDeviceNull () {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel1 = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel1.setHasPassthroughDevice(null);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel1);        
        
        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());

        Assert.assertNull(reports.get(0).getFlagsIMS());
        
    }

    @Test
    public void test_VMDRS_VMHA_BALLOONEDMEM_ENCRYPTEDDISK_OPAQUENETWORK_SRIOV_true() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasVmDrsConfig(true);
        vmWorkloadInventoryModel.setHasVmHaConfig(true);
        vmWorkloadInventoryModel.setBalloonedMemory(1000);
        vmWorkloadInventoryModel.setHasEncryptedDisk(true);
        vmWorkloadInventoryModel.setHasOpaqueNetwork(true);
        vmWorkloadInventoryModel.setHasSriovNic(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(7, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_Opaque_Network", "Flag_Encrypted_Disk", "Flag_Ballooned_Memory", "Flag_VM_HA_Config", "Flag_VM_DRS_Config", "Flag_SR-IOV");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();

        Assert.assertEquals(6, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.VM_DRS_CONFIG_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.VM_HA_CONFIG_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.BALLOONED_MEMORY_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.ENCRYPTED_DISK_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.OPAQUE_NETWORK_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.SR_IOV_NIC_FLAG_NAME));
    }    
    
    @Test
    public void test_VMDRS_VMHA_BALLOONEDMEM_ENCRYPTEDDISK_OPAQUENETWORK_SRIOV_false() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");
      
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasVmDrsConfig(false);
        vmWorkloadInventoryModel.setHasVmHaConfig(false);
        vmWorkloadInventoryModel.setBalloonedMemory(0);
        vmWorkloadInventoryModel.setHasEncryptedDisk(false);
        vmWorkloadInventoryModel.setHasOpaqueNetwork(false);
        vmWorkloadInventoryModel.setHasSriovNic(false);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);
      
         Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNull(report.getFlagsIMS());
    }   


    @Test
    public void test_UEFI_Boot()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setFirmware("EFI");
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);


        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_UEFI_Boot");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.UEFI_BOOT_FLAG_NAME));

    }

@Test
    public void test_hasUSBcontrollersTrue()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasUSBcontrollers(true);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "Flag_USB_Device");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.USB_CONTROLLERS_FLAG_NAME));

    }

    @Test
    public void test_BIOS_Boot()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setFirmware("BIOS");
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);

    }

    @Test
    public void test_firmware_null()
    {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setFirmware(null);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");

        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Set<String> flagsIMS = report.getFlagsIMS();
        Assert.assertNull(flagsIMS);

    }   
    
    @Test
    public void test_CNV_Fields_null() {
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "Flags");

        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setHasVmDrsConfig(null);
        vmWorkloadInventoryModel.setHasVmHaConfig(null);
        vmWorkloadInventoryModel.setBalloonedMemory(null);
        vmWorkloadInventoryModel.setHasEncryptedDisk(null);
        vmWorkloadInventoryModel.setHasOpaqueNetwork(null);
        vmWorkloadInventoryModel.setHasSriovNic(null);
        vmWorkloadInventoryModel.setHasSharedVmdk(null);
        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        WorkloadInventoryReportModel workloadInventoryReportModel = new WorkloadInventoryReportModel();
        facts.put("workloadInventoryReportModel",workloadInventoryReportModel);

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);
        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest");
      
        List<WorkloadInventoryReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, WorkloadInventoryReportModel.class);
        // just one report has to be created
        Assert.assertEquals(1, reports.size());
        WorkloadInventoryReportModel report = reports.get(0);
        Assert.assertNull(report.getFlagsIMS());
    }
}

