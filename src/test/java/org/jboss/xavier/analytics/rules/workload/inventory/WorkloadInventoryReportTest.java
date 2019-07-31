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

import java.math.BigDecimal;
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

    @Test
    public void test() {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.workload.inventory", 4);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        //Basic Fields
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

        //Flags
        vmWorkloadInventoryModel.setNicsCount(5);
        vmWorkloadInventoryModel.setHasRdmDisk(true);

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
        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
       Utils.verifyRulesFiredNames(this.agendaEventListener,
            // BasicFields
            "Copy basic fields and agenda controller",
            // Flags
               "Flag_Nics", "Flag_Rdm_Disk"
            // Targets
            // Complexity
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
        Assert.assertEquals(new Long(100000001).intValue(),workloadInventoryReportModel.getDiskSpace().intValue());
        Assert.assertEquals(4096,workloadInventoryReportModel.getMemory().intValue());
        Assert.assertEquals(4,workloadInventoryReportModel.getCpuCores().intValue());
        Assert.assertEquals("Red Hat Enterprise Linux Server release 7.6 (Maipo)",workloadInventoryReportModel.getOsDescription());
        Assert.assertEquals("RHEL",workloadInventoryReportModel.getOsName());
        // Flags
        Set<String> flagsIMS = workloadInventoryReportModel.getFlagsIMS();
        Assert.assertNotNull(flagsIMS);
        Assert.assertEquals(2, flagsIMS.size());
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME));
        Assert.assertTrue(flagsIMS.contains(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME));
        // Targets
        // Complexity
        // Workloads
    }
}
