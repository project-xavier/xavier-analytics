package org.jboss.xavier.analytics.rules.workload.inventory;

import org.jboss.xavier.analytics.pojo.input.workload.inventory.VMWorkloadInventoryModel;
import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.jboss.xavier.analytics.rules.BaseIntegrationTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;

public class WorkloadInventoryReportTest extends BaseIntegrationTest {

    public WorkloadInventoryReportTest()
    {
        super("WorkloadInventoryKSession0");
    }

    private void checkLoadedRulesNumber()
    {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 22);
    }

    @Test
    public void testNoFlagsSupportedOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(2);
        vmWorkloadInventoryModel.setHasRdmDisk(false);
        List<String> vmDiskFilenames = new ArrayList<>();

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNull(flagsIMS);
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(2, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        Assert.assertTrue(targets.contains("OSP"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testOneFlagSupportedOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Nics",
                // Target
                "Target_RHV",
                // Complexity
                "One_Flag_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME));
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(1, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testMoreThanOneFlagSupportedOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);
        vmWorkloadInventoryModel.setHasRdmDisk(true);
        List<String> vmDiskFilenames = new ArrayList<>();
        vmDiskFilenames.add("/path/to/disk.vdmk");
        vmWorkloadInventoryModel.setVmDiskFilenames(vmDiskFilenames);
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(6, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Nics", "Flag_Rdm_Disk", "Flag_Shared_Disks",
                // Target
                "Target_RHV",
                // Complexity
                "More_Than_One_Flag_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(2, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME));
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(1, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testNoFlagsUnSupportedOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Oracle Enterprise Linux");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("Oracle");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(2);
        vmWorkloadInventoryModel.setHasRdmDisk(false);
        List<String> vmDiskFilenames = new ArrayList<>();

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags

                // Target
                "Target_RHV", "Target_OSP", "Target_Convert2RHEL",
                // Complexity
                "No_Flags_Not_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Oracle Enterprise Linux",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("Oracle",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNull(flagsIMS);
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(3, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        Assert.assertTrue(targets.contains("OSP"));
        Assert.assertTrue(targets.contains("Convert2RHEL"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_MEDIUM,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testFlagsCentOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("CentOS Enterprise Linux");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("CentOS");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);
        vmWorkloadInventoryModel.setHasRdmDisk(true);
        List<String> vmDiskFilenames = new ArrayList<>();

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(6, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Nics", "Flag_Rdm_Disk",
                // Target
                "Target_RHV", "Target_Convert2RHEL",
                // Complexity
                "One_Or_More_Flags_Not_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("CentOS Enterprise Linux",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("CentOS",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertEquals(2, flagsIMS.size());
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(2, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        Assert.assertTrue(targets.contains("Convert2RHEL"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testOneOrMoreFlagsUnsupported_OS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Debian Linux Server");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("debian");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Nics",
                // Target
                "Target_RHV",
                // Complexity
                "One_Or_More_Flags_Not_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Debian Linux Server",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("debian",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(1, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME));
        // Targets
        Set<String> targets = workloadInventoryReportModel.getRecommendedTargetsIMS();
        Assert.assertEquals(1, targets.size());
        Assert.assertTrue(targets.contains("RHV"));
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testUndetectedOS() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Apple OSX");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("OSX");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(2);
        vmWorkloadInventoryModel.setHasRdmDisk(false);
        List<String> vmDiskFilenames = new ArrayList<>();

        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                // Complexity
                "Not_Detected_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Apple OSX",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("OSX",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNull(flagsIMS);
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_UNKNOWN,workloadInventoryReportModel.getComplexity());
        // Workloads
    }

    @Test
    public void testTomcatWorkload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);
        vmWorkloadInventoryModel.setHasRdmDisk(true);
        List<String> vmDiskFilenames = new ArrayList<>();
        vmDiskFilenames.add("/path/to/disk.vdmk");
        vmWorkloadInventoryModel.setVmDiskFilenames(vmDiskFilenames);
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("tomcat");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        files.put("file.txt", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(7, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Nics", "Flag_Rdm_Disk", "Flag_Shared_Disks",
                // Target
                "Target_RHV",
                // Complexity
                "More_Than_One_Flag_Supported_OS",
                // Workloads
                "Workloads_Tomcat"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(2, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME));
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_HARD,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("tomcat")));
    }

    @Test
    public void testEAPWorkload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("jboss");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_EAP"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("Red Hat JBoss EAP".toLowerCase())));
    }

    @Test
    public void testWebsphereWorkload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("Dmgr_was.init");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_Websphere"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("IBM Websphere App Server".toLowerCase())));
    }

    @Test
    public void testWeblogicWorkload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("wls_adminmanager");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_Weblogic"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("Oracle Weblogic".toLowerCase())));
    }

    @Test
    public void testOracleDBWorkload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("dbora");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_Oracle_DB"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("Oracle Database".toLowerCase())));
    }

    @Test
    public void testSAP_HANA_Workload() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("sapinit");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_SAP_HANA"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("SAP HANA".toLowerCase())));
    }

    @Test
    public void testSQLServerOnLinux() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        systemServicesNames.add("mssql-server");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);


        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_Microsoft_SQL_Server_On_Linux"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("Microsoft SQL Server".toLowerCase())));
    }

    @Test
    public void testSQLServerOnWindows() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        List<String> systemServicesNames = new ArrayList<>();
        systemServicesNames.add("unix_service");
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);
        Map<String, String> files = new HashMap<>();
        // TODO remove the wrong test file once it will clear how the rule should work
        // files.put("MSSQLSERVERHOME", "C:\\Program Files\\Microsoft SQL Server");
        files.put("C:\\Program Files\\Microsoft SQL Server", null);
        vmWorkloadInventoryModel.setFiles(files);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS",
                // Workloads
                "Workloads_Microsoft_SQL_Server_On_Windows"
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
        Assert.assertNotNull(workloadInventoryReportModel.getWorkloads());
        Assert.assertEquals(1, workloadInventoryReportModel.getWorkloads().size());
        Assert.assertTrue(workloadInventoryReportModel.getWorkloads().stream().anyMatch(workload -> workload.toLowerCase().contains("Microsoft SQL Server".toLowerCase())));
    }

    @Test
    public void testNoServicesAndNoFiles() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber();

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
        VMWorkloadInventoryModel vmWorkloadInventoryModel = new VMWorkloadInventoryModel();
        vmWorkloadInventoryModel.setProvider("IMS vCenter");
        vmWorkloadInventoryModel.setDatacenter("V2V-DC");
        vmWorkloadInventoryModel.setCluster("Cluster 1");
        vmWorkloadInventoryModel.setVmName("vm tests");
        vmWorkloadInventoryModel.setDiskSpace(100000001L);
        vmWorkloadInventoryModel.setMemory(4096L);
        vmWorkloadInventoryModel.setCpuCores(4);
        vmWorkloadInventoryModel.setGuestOSFullName("Red Hat Enterprise Linux Server release 7.6 (Maipo)");
        // keep it lower case to check that the rules evaluate it ignoring the case
        vmWorkloadInventoryModel.setOsProductName("rhel");
        vmWorkloadInventoryModel.setProduct("VMware vCenter");
        vmWorkloadInventoryModel.setVersion("6.5");
        vmWorkloadInventoryModel.setHost_name("esx13.v2v.bos.redhat.com");

        List<String> vmDiskFilenames = new ArrayList<>();
        vmDiskFilenames.add("/path/to/disk.vdmk");
        vmWorkloadInventoryModel.setVmDiskFilenames(vmDiskFilenames);
/*        List<String> systemServicesNames = new ArrayList<>();
        vmWorkloadInventoryModel.setSystemServicesNames(systemServicesNames);*/
        vmWorkloadInventoryModel.setSystemServicesNames(null);
        /*Map<String, String> files = new HashMap<>();
        vmWorkloadInventoryModel.setFiles(files);*/
        vmWorkloadInventoryModel.setFiles(null);

        facts.put("vmWorkloadInventoryModel", vmWorkloadInventoryModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // add the query to retrieve the report we want
        commands.add(CommandFactory.newQuery(QUERY_IDENTIFIER, "GetWorkloadInventoryReports"));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(5, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // BasicFields
                "Copy basic fields and agenda controller",
                // Flags
                "Flag_Shared_Disks",
                // Target
                "Target_RHV", "Target_OSP",
                // Complexity
                "No_Flag_Supported_OS"
                // Workloads
        );

        // retrieve the QueryResults that was available in the working memory from the results
        QueryResults queryResults= (QueryResults) results.get(QUERY_IDENTIFIER);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, queryResults.size());

        // Check that the object is of the expected type and with the expected identifier (i.e. "report")
        QueryResultsRow queryResultsRow = queryResults.iterator().next();
        Assert.assertThat(queryResultsRow.get("report"), instanceOf(WorkloadInventoryReportModel.class));

        // Check that the object has exactly the fields that the rules tested should add/change
        WorkloadInventoryReportModel workloadInventoryReportModel = (WorkloadInventoryReportModel) queryResultsRow.get("report");
        // BasicFields
        Assert.assertEquals("IMS vCenter",workloadInventoryReportModel.getProvider());
        Assert.assertEquals("V2V-DC",workloadInventoryReportModel.getDatacenter());
        Assert.assertEquals("Cluster 1",workloadInventoryReportModel.getCluster());
        Assert.assertEquals("vm tests",workloadInventoryReportModel.getVmName());
        Assert.assertEquals(100000001L,workloadInventoryReportModel.getDiskSpace(), 0);
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("rhel",workloadInventoryReportModel.getOsName());
        Assert.assertEquals("VMware vCenter", workloadInventoryReportModel.getProduct());
        Assert.assertEquals("6.5", workloadInventoryReportModel.getVersion());
        Assert.assertEquals("esx13.v2v.bos.redhat.com", workloadInventoryReportModel.getHost_name());
        // Flags
        // Targets
        // Complexity
        Assert.assertEquals(WorkloadInventoryReportModel.COMPLEXITY_EASY,workloadInventoryReportModel.getComplexity());
        // Workloads
    }
}
