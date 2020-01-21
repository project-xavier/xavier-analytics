package org.jboss.xavier.analytics.rules.initialcostsaving;

import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.RHVAdditionalContainerCapacityModel;
import org.jboss.xavier.analytics.pojo.output.RHVSavingsModel;
import org.jboss.xavier.analytics.pojo.support.initialcostsaving.PricingDataModel;
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

public class RHVAdditionalContainerCapacityTest  extends BaseTest
{
    public RHVAdditionalContainerCapacityTest()
    {
        // provide the name of the DRL file you want to test
        super("/org/jboss/xavier/analytics/rules/initialcostsaving/RHVAdditionalContainerCapacity.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.initialcostsaving", 1);
    }

    @Test
    public void test()
    {
        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVAdditionalContainerCapacity");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setRhOSListValue(15000.0);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        RHVSavingsModel savingsModel = new RHVSavingsModel();
        savingsModel.setRhvSaveHighValue(1652480.0);
        savingsModel.setRhvSaveLikelyValue(1127480.0);
        savingsModel.setRhvSaveLowValue(602480.0);

        // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setRhvSavingsModel(savingsModel);
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
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "RHVAdditionalContainerCapacity");

        // this method retrieves the List of Objects that were available in the working memory from the results
        // and filters the type of object you're interested in retrieving (e.g. InitialSavingsEstimationReportModel)
        List<InitialSavingsEstimationReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, InitialSavingsEstimationReportModel.class);

        // Check that the number of object is the right one (in this case, there must be just one report)
        Assert.assertEquals(1, reports.size());

        // Check that the object has exactly the fields that the rule tested should add/change
        InitialSavingsEstimationReportModel report = reports.get(0);
        RHVAdditionalContainerCapacityModel model = report.getRhvAdditionalContainerCapacityModel();
        Assert.assertEquals(11017, model.getRhvContainerHigh(), 0);
        Assert.assertEquals(7517, model.getRhvContainerLikely(), 0);
        Assert.assertEquals(4017, model.getRhvContainerLow(), 0);
    }
}

