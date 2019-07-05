package org.jboss.xavier.analytics.test;

import org.jboss.xavier.analytics.pojo.output.EnvironmentModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.SourceCostsModel;
import org.jboss.xavier.analytics.pojo.output.SourceRampDownCostsModel;
import org.jboss.xavier.analytics.pojo.support.PricingDataModel;

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

public class SourceRampDownCostsTest extends BaseTest
{
    public SourceRampDownCostsTest()
    {
        // provide the name of the DRL file you want to test
        super("/org/jboss/xavier/analytics/rules/SourceRampDownCosts.drl", ResourceType.DRL);
    }

    @Test
    public void test()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber(1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "SourceRampDownCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setSourceListValue(3495.0);
        pricingDataModel.setSourceDiscountPercentage(0.5);
        pricingDataModel.setSourceMaintenancePercentage(0.2);
        pricingDataModel.setSourceMaintenanceGrowthPercentage(0.05);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        // another object needed as input for the test
        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setHypervisors(500);
        environmentModel.setYear1Hypervisor(300);
        environmentModel.setYear2Hypervisor(100);
        environmentModel.setYear3Hypervisor(100);

        SourceCostsModel sourceCosts = new SourceCostsModel();
        sourceCosts.setSourceMaintenanceValue(350.0);

        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
        reportModel.setSourceCostsModel(sourceCosts);
        // added to the facts Map
        facts.put("reportModel", reportModel);

        // define the list of commands you want to be executed by Drools
        List<Command> commands = new ArrayList<>();
        // first generate and add all of the facts created above
        commands.addAll(Utils.newInsertCommands(facts));
        // then generate the 'fireAllRules' command
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        // last create the command to retrieve the objects available in
        // the working memory at the end of the rules' execution
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        // execute the commands in the KIE session and get the results
        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        // check that the number of rules fired is what you expect
        Assert.assertEquals(2, results.get(NUMBER_OF_FIRED_RULE_KEY));

        // retrieve the List of Objects that were available in the working memory from the results
        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        // filter the type of object you're interested in checking (e.g. InitialSavingsEstimationReportModel)
        List<InitialSavingsEstimationReportModel> reports = objects.stream()
                .filter(object -> object instanceof InitialSavingsEstimationReportModel)
                .map(object -> (InitialSavingsEstimationReportModel) object)
                .collect(Collectors.toList());

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, reports.size());

        // Check that the object has exactly the fields that the rule tested should add/change
        InitialSavingsEstimationReportModel report = reports.get(0);
        SourceRampDownCostsModel sourceRampDownCostsModel = report.getSourceRampDownCostsModel();

        Assert.assertEquals(300, sourceRampDownCostsModel.getYear1ServersOffSource().intValue());
        Assert.assertEquals(400, sourceRampDownCostsModel.getYear2ServersOffSource().intValue());
        Assert.assertEquals(500, sourceRampDownCostsModel.getYear3ServersOffSource().intValue());

        Assert.assertEquals(200, sourceRampDownCostsModel.getYear1SourceActiveLicense().intValue());
        Assert.assertEquals(100, sourceRampDownCostsModel.getYear2SourceActiveLicense().intValue());
        Assert.assertEquals(0, sourceRampDownCostsModel.getYear3SourceActiveLicense().intValue());

        Assert.assertEquals(500, sourceRampDownCostsModel.getYear1SourcePaidMaintenance().intValue());
        Assert.assertEquals(500, sourceRampDownCostsModel.getYear2SourcePaidMaintenance().intValue());
        Assert.assertEquals(500, sourceRampDownCostsModel.getYear3SourcePaidMaintenance().intValue());

        Assert.assertEquals(700, sourceRampDownCostsModel.getYear1SourceMaintenancePerServerValue(), 0);
        Assert.assertEquals(735, sourceRampDownCostsModel.getYear2SourceMaintenancePerServerValue(), 0);
        Assert.assertEquals(772, sourceRampDownCostsModel.getYear3SourceMaintenancePerServerValue(), 0);

        Assert.assertEquals(350000, sourceRampDownCostsModel.getYear1SourceMaintenanceTotalValue(), 0);
        Assert.assertEquals(367500, sourceRampDownCostsModel.getYear2SourceMaintenanceTotalValue(), 0);
        Assert.assertEquals(386000, sourceRampDownCostsModel.getYear3SourceMaintenanceTotalValue(), 0);
    }
}

