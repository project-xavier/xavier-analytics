package org.jboss.xavier.analytics.test;

import org.jboss.xavier.analytics.pojo.output.*;
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

import static org.jboss.xavier.analytics.functions.HelperFunctions.round;

public class RHVRampUpCostsTest extends BaseTest
{
    public RHVRampUpCostsTest()
    {
        // provide the name of the DRL file you want to test
        super("/org/jboss/xavier/analytics/rules/RHVRampUpCosts.drl", ResourceType.DRL);
    }

    @Test
    public void testVSphereNoFreeSubs()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber(1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVRampUpCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setFreeSubsYear1Indicator(false);
        pricingDataModel.setFreeSubsYear2And3Indicator(false);
        pricingDataModel.setRhvConsultValue(288600.0);
        pricingDataModel.setRhLearningSubsValue(28000.0);
        pricingDataModel.setRhvTAndEValue(55000.0);
        pricingDataModel.setRhvValue(0.73);
        pricingDataModel.setRhvListValue(1498.0);
        pricingDataModel.setRhVirtValue(0.40);
        pricingDataModel.setRhVirtListValue(2798.0);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setHypervisors(500);
        environmentModel.setYear1Hypervisor(300);
        environmentModel.setYear2Hypervisor(100);
        environmentModel.setYear3Hypervisor(100);
        environmentModel.setSourceProductIndicator(1);
        environmentModel.setGrowthRatePercentage(.05);

        SourceRampDownCostsModel sourceRampDownCosts = new SourceRampDownCostsModel();
        sourceRampDownCosts.setYear1SourcePaidMaintenance(500);
        sourceRampDownCosts.setYear2SourcePaidMaintenance(500);

        // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
        reportModel.setSourceRampDownCostsModel(sourceRampDownCosts);
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
        RHVRampUpCostsModel rhvRampUpCostsModel = report.getRhvRampUpCostsModel();
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvServers(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvServers(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvServers(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubs(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvPaidSubs(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvPaidSubs(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvPaidSubs(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear1RhvPerServerValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear2RhvPerServerValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear3RhvPerServerValue(), 0.01);
        Assert.assertEquals(121200, rhvRampUpCostsModel.getYear1RhvTotalValue(), 0.01);
        Assert.assertEquals(161600, rhvRampUpCostsModel.getYear2RhvTotalValue(), 0.01);
        Assert.assertEquals(202000, rhvRampUpCostsModel.getYear3RhvTotalValue(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvServersGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvServersGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvServersGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear1RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear2RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear3RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(10100.0, rhvRampUpCostsModel.getYear1RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(20604.0, rhvRampUpCostsModel.getYear2RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(31916.0, rhvRampUpCostsModel.getYear3RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(131300.0, rhvRampUpCostsModel.getYear1RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(182204.0, rhvRampUpCostsModel.getYear2RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(233916.0, rhvRampUpCostsModel.getYear3RhvGrandTotalGrowthValue(), 0.01);


        Assert.assertEquals(28000, rhvRampUpCostsModel.getRhvSwitchLearningSubsValue(), 0.01);
        Assert.assertEquals(288600, rhvRampUpCostsModel.getRhvSwitchConsultValue(), 0.01);
        Assert.assertEquals(55000, rhvRampUpCostsModel.getRhvSwitchTAndEValue(), 0.01);
    }

    @Test
    public void testVSphereWithFreeSubs()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber(1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVRampUpCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setFreeSubsYear1Indicator(true);
        pricingDataModel.setFreeSubsYear2And3Indicator(true);
        pricingDataModel.setRhvConsultValue(288600.0);
        pricingDataModel.setRhLearningSubsValue(28000.0);
        pricingDataModel.setRhvTAndEValue(55000.0);
        pricingDataModel.setRhvValue(0.73);
        pricingDataModel.setRhvListValue(1498.0);
        pricingDataModel.setRhVirtValue(0.40);
        pricingDataModel.setRhVirtListValue(2798.0);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setHypervisors(500);
        environmentModel.setYear1Hypervisor(300);
        environmentModel.setYear2Hypervisor(100);
        environmentModel.setYear3Hypervisor(100);
        environmentModel.setSourceProductIndicator(1);
        environmentModel.setGrowthRatePercentage(.05);

        SourceRampDownCostsModel sourceRampDownCosts = new SourceRampDownCostsModel();
        sourceRampDownCosts.setYear1SourcePaidMaintenance(500);
        sourceRampDownCosts.setYear2SourcePaidMaintenance(500);

        // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
        reportModel.setSourceRampDownCostsModel(sourceRampDownCosts);
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
        RHVRampUpCostsModel rhvRampUpCostsModel = report.getRhvRampUpCostsModel();
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvServers(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvServers(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvServers(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvCompSubs(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear2RhvCompSubs(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear3RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubs(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear2RhvPaidSubs(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear3RhvPaidSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPerServerValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear2RhvPerServerValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear3RhvPerServerValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalValue(), 0.01);
        Assert.assertEquals(121200, rhvRampUpCostsModel.getYear2RhvTotalValue(), 0.01);
        Assert.assertEquals(161600, rhvRampUpCostsModel.getYear3RhvTotalValue(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvServersGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvServersGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvServersGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(26, rhvRampUpCostsModel.getYear2RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(28, rhvRampUpCostsModel.getYear3RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear2RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear3RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear1RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear2RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(404.0, rhvRampUpCostsModel.getYear3RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(10100.0, rhvRampUpCostsModel.getYear2RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(20604.0, rhvRampUpCostsModel.getYear3RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(131300.0, rhvRampUpCostsModel.getYear2RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(182204.0, rhvRampUpCostsModel.getYear3RhvGrandTotalGrowthValue(), 0.01);

        Assert.assertEquals(28000, rhvRampUpCostsModel.getRhvSwitchLearningSubsValue(), 0.01);
        Assert.assertEquals(288600, rhvRampUpCostsModel.getRhvSwitchConsultValue(), 0.01);
        Assert.assertEquals(55000, rhvRampUpCostsModel.getRhvSwitchTAndEValue(), 0.01);
    }

    @Test
    public void testVCloudNoFreeSubs()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber(1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVRampUpCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setFreeSubsYear1Indicator(false);
        pricingDataModel.setFreeSubsYear2And3Indicator(false);
        pricingDataModel.setRhvConsultValue(288600.0);
        pricingDataModel.setRhLearningSubsValue(28000.0);
        pricingDataModel.setRhvTAndEValue(55000.0);
        pricingDataModel.setRhvValue(0.73);
        pricingDataModel.setRhvListValue(1498.0);
        pricingDataModel.setRhVirtValue(0.40);
        pricingDataModel.setRhVirtListValue(2798.0);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setHypervisors(500);
        environmentModel.setYear1Hypervisor(300);
        environmentModel.setYear2Hypervisor(100);
        environmentModel.setYear3Hypervisor(100);
        environmentModel.setSourceProductIndicator(2);
        environmentModel.setGrowthRatePercentage(.05);

        SourceRampDownCostsModel sourceRampDownCosts = new SourceRampDownCostsModel();
        sourceRampDownCosts.setYear1SourcePaidMaintenance(500);
        sourceRampDownCosts.setYear2SourcePaidMaintenance(500);

        // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
        reportModel.setSourceRampDownCostsModel(sourceRampDownCosts);
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
        RHVRampUpCostsModel rhvRampUpCostsModel = report.getRhvRampUpCostsModel();
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvServers(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvServers(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvServers(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubs(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvPaidSubs(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvPaidSubs(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvPaidSubs(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear1RhvPerServerValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear2RhvPerServerValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear3RhvPerServerValue(), 0.01);
        Assert.assertEquals(503700, rhvRampUpCostsModel.getYear1RhvTotalValue(), 0.01);
        Assert.assertEquals(671600, rhvRampUpCostsModel.getYear2RhvTotalValue(), 0.01);
        Assert.assertEquals(839500, rhvRampUpCostsModel.getYear3RhvTotalValue(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvServersGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvServersGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvServersGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear1RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear2RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear3RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(41975.0, rhvRampUpCostsModel.getYear1RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(85629.0, rhvRampUpCostsModel.getYear2RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(132641.0, rhvRampUpCostsModel.getYear3RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(545675.0, rhvRampUpCostsModel.getYear1RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(757229.0, rhvRampUpCostsModel.getYear2RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(972141.0, rhvRampUpCostsModel.getYear3RhvGrandTotalGrowthValue(), 0.01);


        Assert.assertEquals(28000, rhvRampUpCostsModel.getRhvSwitchLearningSubsValue(), 0.01);
        Assert.assertEquals(288600, rhvRampUpCostsModel.getRhvSwitchConsultValue(), 0.01);
        Assert.assertEquals(55000, rhvRampUpCostsModel.getRhvSwitchTAndEValue(), 0.01);
    }

    @Test
    public void testVCloudWithFreeSubs()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        checkLoadedRulesNumber(1);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();
        // always add a String fact with the name of the agenda group defined in the DRL file (e.g. "SourceCosts")
        facts.put("agendaGroup", "RHVRampUpCosts");

        // define the objects needed to define the "When" side of the test
        PricingDataModel pricingDataModel = new PricingDataModel();
        pricingDataModel.setFreeSubsYear1Indicator(true);
        pricingDataModel.setFreeSubsYear2And3Indicator(true);
        pricingDataModel.setRhvConsultValue(288600.0);
        pricingDataModel.setRhLearningSubsValue(28000.0);
        pricingDataModel.setRhvTAndEValue(55000.0);
        pricingDataModel.setRhvValue(0.73);
        pricingDataModel.setRhvListValue(1498.0);
        pricingDataModel.setRhVirtValue(0.40);
        pricingDataModel.setRhVirtListValue(2798.0);
        // and add each object to the Map
        facts.put("pricingDataModel", pricingDataModel);

        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setHypervisors(500);
        environmentModel.setYear1Hypervisor(300);
        environmentModel.setYear2Hypervisor(100);
        environmentModel.setYear3Hypervisor(100);
        environmentModel.setSourceProductIndicator(2);
        environmentModel.setGrowthRatePercentage(.05);

        SourceRampDownCostsModel sourceRampDownCosts = new SourceRampDownCostsModel();
        sourceRampDownCosts.setYear1SourcePaidMaintenance(500);
        sourceRampDownCosts.setYear2SourcePaidMaintenance(500);

        // another object needed as input for the test
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);
        reportModel.setSourceRampDownCostsModel(sourceRampDownCosts);
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
        RHVRampUpCostsModel rhvRampUpCostsModel = report.getRhvRampUpCostsModel();
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvServers(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvServers(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvServers(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvCompSubs(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear2RhvCompSubs(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear3RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubs(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear2RhvPaidSubs(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear3RhvPaidSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPerServerValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear2RhvPerServerValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear3RhvPerServerValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalValue(), 0.01);
        Assert.assertEquals(503700, rhvRampUpCostsModel.getYear2RhvTotalValue(), 0.01);
        Assert.assertEquals(671600, rhvRampUpCostsModel.getYear3RhvTotalValue(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvServersGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear2RhvServersGrowth(), 0.01);
        Assert.assertEquals(79, rhvRampUpCostsModel.getYear3RhvServersGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear1RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(26, rhvRampUpCostsModel.getYear2RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(28, rhvRampUpCostsModel.getYear3RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(25, rhvRampUpCostsModel.getYear2RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(51, rhvRampUpCostsModel.getYear3RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear1RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear2RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(1679.0, rhvRampUpCostsModel.getYear3RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(41975.0, rhvRampUpCostsModel.getYear2RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(85629.0, rhvRampUpCostsModel.getYear3RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(545675.0, rhvRampUpCostsModel.getYear2RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(757229.0, rhvRampUpCostsModel.getYear3RhvGrandTotalGrowthValue(), 0.01);


        Assert.assertEquals(28000, rhvRampUpCostsModel.getRhvSwitchLearningSubsValue(), 0.01);
        Assert.assertEquals(288600, rhvRampUpCostsModel.getRhvSwitchConsultValue(), 0.01);
        Assert.assertEquals(55000, rhvRampUpCostsModel.getRhvSwitchTAndEValue(), 0.01);
    }
}
