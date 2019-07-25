package org.jboss.xavier.analytics.rules.initialcostsaving;

import org.jboss.xavier.analytics.pojo.output.EnvironmentModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.output.SourceCostsModel;
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
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;

public class SourceCostsTest extends BaseTest
{
    public SourceCostsTest()
    {
        // provide the name of the DRL file you want to test
        super("/org/jboss/xavier/analytics/rules/initialcostsaving/SourceCosts.drl", ResourceType.DRL);
    }

    @Test
    public void test_SourceNewELAIndicator_0()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.initialcostsaving", 4);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "SourceCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setSourceListValue(1000.0);
        pricingDataModel.setSourceDiscountPercentage(0.2);
        pricingDataModel.setSourceMaintenancePercentage(0.7);
        pricingDataModel.setSourceRenewHighFactor(3.2);
        pricingDataModel.setSourceRenewLikelyFactor(2.0);
        pricingDataModel.setSourceRenewLowFactor(1.7);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        // another object needed as input for the test
        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setGrowthRatePercentage(0.05);
        environmentModel.setHypervisors(500);

        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
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
        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "SourceCostsRules_0", "SourceCostsRules_sourceNewELAIndicator_0");

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
        SourceCostsModel sourceCostsModel = report.getSourceCostsModel();
        Assert.assertEquals(800, sourceCostsModel.getSourceLicenseValue(), 0);
        Assert.assertEquals(560, sourceCostsModel.getSourceMaintenanceValue(), 0);

        Assert.assertEquals(525, sourceCostsModel.getYear1Server().intValue());
        Assert.assertEquals(551, sourceCostsModel.getYear2Server().intValue());
        Assert.assertEquals(579, sourceCostsModel.getYear3Server().intValue());

        Assert.assertEquals(40000.0, sourceCostsModel.getYear1SourceValue(), 0);
        Assert.assertEquals(41600.0, sourceCostsModel.getYear2SourceValue(), 0);
        Assert.assertEquals(44800.0, sourceCostsModel.getYear3SourceValue(), 0);
        Assert.assertEquals(126400.0, sourceCostsModel.getTotSourceValue(), 0);

        Assert.assertEquals(367500.0, sourceCostsModel.getYear1SourceMaintenanceValue(), 0);
        Assert.assertEquals(385700.0, sourceCostsModel.getYear2SourceMaintenanceValue(), 0);
        Assert.assertEquals(405300.0, sourceCostsModel.getYear3SourceMaintenanceValue(), 0);
        Assert.assertEquals(1158500.0, sourceCostsModel.getTotSourceMaintenanceValue(), 0);

        Assert.assertEquals(1284900.0, sourceCostsModel.getTotalSourceValue(), 0);

        Assert.assertEquals(7056000.0, sourceCostsModel.getSourceNewHighValue(), 0);
        Assert.assertEquals(5040000.0, sourceCostsModel.getSourceNewLikelyValue(), 0);
        Assert.assertEquals(4536000.0, sourceCostsModel.getSourceNewLowValue(), 0);

        Assert.assertEquals(0, sourceCostsModel.getSourceNewELAIndicator().intValue());
        Assert.assertEquals(7056000.0, sourceCostsModel.getSourceRenewHighValue(), 0);
        Assert.assertEquals(5040000.0, sourceCostsModel.getSourceRenewLikelyValue(), 0);
        Assert.assertEquals(4536000.0, sourceCostsModel.getSourceRenewLowValue(), 0);
    }

    @Test
    public void test_SourceNewELAIndicator_1()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.initialcostsaving", 4);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "SourceCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setSourceListValue(1000.0);
        pricingDataModel.setSourceDiscountPercentage(0.6);
        pricingDataModel.setSourceMaintenancePercentage(0.3);
        pricingDataModel.setSourceRenewHighFactor(2.5);
        pricingDataModel.setSourceRenewLikelyFactor(2.0);
        pricingDataModel.setSourceRenewLowFactor(1.5);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        // another object needed as input for the test
        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setGrowthRatePercentage(0.2);
        environmentModel.setHypervisors(500);

        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
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
        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "SourceCostsRules_0", "SourceCostsRules_sourceNewELAIndicator_1");

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
        SourceCostsModel sourceCostsModel = report.getSourceCostsModel();
        Assert.assertEquals(400, sourceCostsModel.getSourceLicenseValue(), 0);
        Assert.assertEquals(120, sourceCostsModel.getSourceMaintenanceValue(), 0);

        Assert.assertEquals(600, sourceCostsModel.getYear1Server().intValue());
        Assert.assertEquals(720, sourceCostsModel.getYear2Server().intValue());
        Assert.assertEquals(864, sourceCostsModel.getYear3Server().intValue());

        Assert.assertEquals(80000.0, sourceCostsModel.getYear1SourceValue(), 0);
        Assert.assertEquals(96000.0, sourceCostsModel.getYear2SourceValue(), 0);
        Assert.assertEquals(115200.0, sourceCostsModel.getYear3SourceValue(), 0);
        Assert.assertEquals(291200.0, sourceCostsModel.getTotSourceValue(), 0);

        Assert.assertEquals(180000.0, sourceCostsModel.getYear1SourceMaintenanceValue(), 0);
        Assert.assertEquals(216000.0, sourceCostsModel.getYear2SourceMaintenanceValue(), 0);
        Assert.assertEquals(259200.0, sourceCostsModel.getYear3SourceMaintenanceValue(), 0);
        Assert.assertEquals(655200.0, sourceCostsModel.getTotSourceMaintenanceValue(), 0);

        Assert.assertEquals(946400.0, sourceCostsModel.getTotalSourceValue(), 0);

        Assert.assertEquals(1260000.0, sourceCostsModel.getSourceNewHighValue(), 0);
        Assert.assertEquals(1080000.0, sourceCostsModel.getSourceNewLikelyValue(), 0);
        Assert.assertEquals(900000.0, sourceCostsModel.getSourceNewLowValue(), 0);

        Assert.assertEquals(1, sourceCostsModel.getSourceNewELAIndicator().intValue());
        Assert.assertEquals(1260000.0, sourceCostsModel.getSourceRenewHighValue(), 0);
        Assert.assertEquals(946400.0, sourceCostsModel.getSourceRenewLikelyValue(), 0);
        Assert.assertEquals(900000.0, sourceCostsModel.getSourceRenewLowValue(), 0);
    }

    @Test
    public void test_SourceNewELAIndicator_2()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.initialcostsaving", 4);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "SourceCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setSourceListValue(1000.0);
        pricingDataModel.setSourceDiscountPercentage(0.6);
        pricingDataModel.setSourceMaintenancePercentage(0.1);
        pricingDataModel.setSourceRenewHighFactor(2.5);
        pricingDataModel.setSourceRenewLikelyFactor(2.0);
        pricingDataModel.setSourceRenewLowFactor(1.5);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        // another object needed as input for the test
        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setGrowthRatePercentage(0.2);
        environmentModel.setHypervisors(500);

        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
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
        Assert.assertEquals(3, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener, "AgendaFocusForTest", "SourceCostsRules_0", "SourceCostsRules_sourceNewELAIndicator_2");

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
        SourceCostsModel sourceCostsModel = report.getSourceCostsModel();
        Assert.assertEquals(400, sourceCostsModel.getSourceLicenseValue(), 0);
        Assert.assertEquals(40, sourceCostsModel.getSourceMaintenanceValue(), 0);

        Assert.assertEquals(600, sourceCostsModel.getYear1Server().intValue());
        Assert.assertEquals(720, sourceCostsModel.getYear2Server().intValue());
        Assert.assertEquals(864, sourceCostsModel.getYear3Server().intValue());

        Assert.assertEquals(80000.0, sourceCostsModel.getYear1SourceValue(), 0);
        Assert.assertEquals(96000.0, sourceCostsModel.getYear2SourceValue(), 0);
        Assert.assertEquals(115200.0, sourceCostsModel.getYear3SourceValue(), 0);
        Assert.assertEquals(291200.0, sourceCostsModel.getTotSourceValue(), 0);

        Assert.assertEquals(60000.0, sourceCostsModel.getYear1SourceMaintenanceValue(), 0);
        Assert.assertEquals(72000.0, sourceCostsModel.getYear2SourceMaintenanceValue(), 0);
        Assert.assertEquals(86400.0, sourceCostsModel.getYear3SourceMaintenanceValue(), 0);
        Assert.assertEquals(218400.0, sourceCostsModel.getTotSourceMaintenanceValue(), 0);

        Assert.assertEquals(509600.0, sourceCostsModel.getTotalSourceValue(), 0);

        Assert.assertEquals(420000.0, sourceCostsModel.getSourceNewHighValue(), 0);
        Assert.assertEquals(360000.0, sourceCostsModel.getSourceNewLikelyValue(), 0);
        Assert.assertEquals(300000.0, sourceCostsModel.getSourceNewLowValue(), 0);

        Assert.assertEquals(2, sourceCostsModel.getSourceNewELAIndicator().intValue());
        Assert.assertEquals(509600.0, sourceCostsModel.getSourceRenewHighValue(), 0);
        Assert.assertEquals(509600.0, sourceCostsModel.getSourceRenewLikelyValue(), 0);
        Assert.assertEquals(509600.0, sourceCostsModel.getSourceRenewLowValue(), 0);
    }
}
