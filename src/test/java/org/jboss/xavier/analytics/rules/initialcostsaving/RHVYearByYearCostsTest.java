package org.jboss.xavier.analytics.rules.initialcostsaving;

import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.RHVRampUpCostsModel;
import org.jboss.xavier.analytics.pojo.output.RHVYearByYearCostsModel;
import org.jboss.xavier.analytics.pojo.output.SourceCostsModel;
import org.jboss.xavier.analytics.pojo.output.SourceRampDownCostsModel;
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

public class RHVYearByYearCostsTest extends BaseTest
{
    public RHVYearByYearCostsTest()
    {
        // provide the name of the DRL file you want to test
        super("/org/jboss/xavier/analytics/rules/initialcostsaving/RHVYearByYearCosts.drl", ResourceType.DRL);
    }

    @Test
    public void test()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.initialcostsaving", 1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVYearByYearCosts");

        SourceRampDownCostsModel sourceRampDownCosts = new SourceRampDownCostsModel();
        sourceRampDownCosts.setYear1SourceMaintenanceTotalValue(120000.0);
        sourceRampDownCosts.setYear2SourceMaintenanceTotalValue(126000.0);
        sourceRampDownCosts.setYear3SourceMaintenanceTotalValue(132300.0);

        RHVRampUpCostsModel rhvRampUpCosts = new RHVRampUpCostsModel();
        rhvRampUpCosts.setYear1RhvTotalValue(121338.0);
        rhvRampUpCosts.setYear1RhvTotalGrowthValue(40446.0);
        rhvRampUpCosts.setRhvSwitchLearningSubsValue(28000.0);
        rhvRampUpCosts.setRhvSwitchConsultValue(288600.0);
        rhvRampUpCosts.setRhvSwitchTAndEValue(55000.0);
        rhvRampUpCosts.setYear2RhvTotalValue(161784.0);
        rhvRampUpCosts.setYear2RhvTotalGrowthValue(88981.0);
        rhvRampUpCosts.setYear3RhvTotalValue(202230.0);
        rhvRampUpCosts.setYear3RhvTotalGrowthValue(147223.0);

        SourceCostsModel sourceCosts = new SourceCostsModel();
        sourceCosts.setSourceRenewHighValue(1260000.0);
        sourceCosts.setSourceRenewLikelyValue(946400.0);
        sourceCosts.setSourceRenewLowValue(900000.0);

                // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setSourceRampDownCostsModel(sourceRampDownCosts);
        reportModel.setRhvRampUpCostsModel(rhvRampUpCosts);
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
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "RHVYearByYearCostsRules");

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
        RHVYearByYearCostsModel rhvYearByYearCostsModel = report.getRhvYearByYearCostsModel();
        Assert.assertEquals(653384.0, rhvYearByYearCostsModel.getYear1RhvGrandTotalValue(), 0);
        Assert.assertEquals(376765.0, rhvYearByYearCostsModel.getYear2RhvGrandTotalValue(), 0);
        Assert.assertEquals(481753.0, rhvYearByYearCostsModel.getYear3RhvGrandTotalValue(), 0);

        Assert.assertEquals(-233384.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(-337917.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(-353384.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedLowValue(), 0);

        Assert.assertEquals(43235.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(-61298.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(-76765.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedLowValue(), 0);

        Assert.assertEquals(-61753.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(-166286.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(-181753.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedLowValue(), 0);

    }
}
