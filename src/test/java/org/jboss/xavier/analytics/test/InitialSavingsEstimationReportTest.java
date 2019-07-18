package org.jboss.xavier.analytics.test;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.analytics.pojo.output.*;
import org.jboss.xavier.analytics.pojo.support.PricingDataModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class InitialSavingsEstimationReportTest
{
    private static final String GET_OBJECTS_KEY = "_getObjects";
    private static final String NUMBER_OF_FIRED_RULE_KEY = "numberOfFiredRules";

    private StatelessKieSession kieSession;
    private KieFileSystem kieFileSystem;
    private KieServices kieServices;

    private AgendaEventListener agendaEventListener = mock( AgendaEventListener.class );

    @Before
    public void setup()
    {
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();
        try {
            Files.walk(Paths.get(ClassLoader.getSystemResource("org/jboss/xavier/analytics/rules/").toURI()))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    File ruleFile = new File(file.toUri());
                    if (ResourceType.DRL.matchesExtension(ruleFile.getName()))
                    {
                        kieFileSystem.write(ResourceFactory.newFileResource(ruleFile).setResourceType(ResourceType.DRL));
                    }
                    else if (ResourceType.DTABLE.matchesExtension(ruleFile.getName()))
                    {
                        FileInputStream ruleFileInputStream = null;
                        try {
                            ruleFileInputStream = new FileInputStream(ruleFile.getPath());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        kieFileSystem.write(ResourceType.DTABLE.getDefaultPath() + ruleFile,
                                kieServices.getResources().newInputStreamResource(ruleFileInputStream).setResourceType(ResourceType.DTABLE));
                    }
                    else
                    {
                        throw new IllegalArgumentException("File '" + ruleFile + "' is not a known resource");
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        List<Message> errorMessages = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        errorMessages.forEach(System.err::println);
        assertEquals(0, errorMessages.size());

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        kieSession = kContainer.newStatelessKieSession();
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        kieSession.addEventListener(new DebugAgendaEventListener());
        kieSession.addEventListener(agendaEventListener);
    }

    @Test
    public void test_SourceNewELAIndicator_0()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules", 25);

        // create a Map with the facts (i.e. Objects) you want to put in the working memory
        Map<String, Object> facts = new HashMap<>();

        UploadFormInputDataModel inputDataModel = new UploadFormInputDataModel();
        inputDataModel.setCustomerId("abc123");
        inputDataModel.setHypervisor(500);
        inputDataModel.setGrowthRatePercentage(0.2);
        inputDataModel.setYear1HypervisorPercentage(0.6);
        inputDataModel.setYear2HypervisorPercentage(0.2);
        inputDataModel.setYear3HypervisorPercentage(0.2);
        inputDataModel.setFileName("test_file_name");
        facts.put("inputDataModel", inputDataModel);

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
        Assert.assertEquals(20, results.get(NUMBER_OF_FIRED_RULE_KEY));
        // check the names of the rules fired are what you expect
        Utils.verifyRulesFiredNames(this.agendaEventListener,
                // Environment
                "Copy input fields and agenda controller",
                // Pricing
                "Source Pricing_18", "Source ELA Terms_26", "Source Renewal Goals_33", "Vendor Maintenance Price Growth_40", "Switching Costs_47", "Consulting_54", "Learning_61", "Red Hat Virtualization_68", "Red Hat CloudForms_75", "Red Hat OpenShift_82", "Red Hat Virtualization Suite_89", "Free Red Hat subscriptions_96", "Red Hat Pricing Effective_103",
                // SourceCosts
                "SourceCostsRules_0", "SourceCostsRules_sourceNewELAIndicator_0",
                // SourceRampDownCosts
                "SourceRampDownCostsRules",
                // RHVRampUpCosts
                "RHVRampUpCosts",
                // RHVYearByYearCosts
                 "RHVYearByYearCostsRules",
                // RHVSavings
                 "RHVSavingsRules"
                // RHVAdditionalContainerCapacity
                // RHVOrderForm
                );

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

        // Environment
        Assert.assertEquals("abc123", report.getCustomerId());
        Assert.assertNotNull(report.getCreationDate());
        Assert.assertEquals("test_file_name", report.getFileName());

        EnvironmentModel environmentModel = report.getEnvironmentModel();
        Assert.assertEquals(500, environmentModel.getHypervisors().intValue());
        Assert.assertEquals(0.2, environmentModel.getGrowthRatePercentage(), 0);
        Assert.assertEquals(300, environmentModel.getYear1Hypervisor().intValue());
        Assert.assertEquals(100, environmentModel.getYear2Hypervisor().intValue());
        Assert.assertEquals(100, environmentModel.getYear3Hypervisor().intValue());
        Assert.assertEquals(1, environmentModel.getSourceProductIndicator().intValue());
        Assert.assertEquals(1, environmentModel.getDealIndicator().intValue());
        Assert.assertEquals(true, environmentModel.getOpenStackIndicator());

        // Pricing
        List<PricingDataModel> pricingDataModelList = objects.stream()
                .filter(object -> object instanceof PricingDataModel)
                .map(object -> (PricingDataModel) object)
                .collect(Collectors.toList());

        Assert.assertEquals(1, pricingDataModelList.size());
        PricingDataModel pricingDataModel = pricingDataModelList.get(0);
        Assert.assertEquals(10000, pricingDataModel.getSourceListValue(), 0);
        Assert.assertEquals(0.1, pricingDataModel.getSourceDiscountPercentage(), 0);
        Assert.assertEquals(0.75, pricingDataModel.getSourceMaintenancePercentage(), 0);
        Assert.assertEquals(3.5, pricingDataModel.getSourceRenewHighFactor(), 0);
        Assert.assertEquals(2.75, pricingDataModel.getSourceRenewLikelyFactor(), 0);
        Assert.assertEquals(2.0, pricingDataModel.getSourceRenewLowFactor(), 0);
        Assert.assertEquals(0.15, pricingDataModel.getSourceMaintenanceGrowthPercentage(), 0);
        Assert.assertEquals(100000, pricingDataModel.getRhvConsultValue(), 0);
        Assert.assertEquals(30000, pricingDataModel.getRhvTAndEValue(), 0);
        Assert.assertEquals(30000, pricingDataModel.getRhLearningSubsValue(), 0);
        Assert.assertEquals(1500, pricingDataModel.getRhvListValue(), 0);
        Assert.assertEquals(0.85, pricingDataModel.getRhvDiscountPercentage(), 0);
        Assert.assertEquals(2400, pricingDataModel.getRhCFListValue(), 0);
        Assert.assertEquals(0.6, pricingDataModel.getRhCFDiscountPercentage(), 0);
        Assert.assertEquals(15000, pricingDataModel.getRhOSListValue(), 0);
        Assert.assertEquals(0.0, pricingDataModel.getRhOSDiscountPercentage(), 0);
        Assert.assertEquals(2800, pricingDataModel.getRhVirtListValue(), 0);
        Assert.assertEquals(0.5, pricingDataModel.getRhVirtDiscountPercentage(), 0);
        Assert.assertEquals(true, pricingDataModel.getFreeSubsYear1Indicator());
        Assert.assertEquals(false, pricingDataModel.getFreeSubsYear2And3Indicator());
        Assert.assertEquals(0.75, pricingDataModel.getRhvValue(), 0);
        Assert.assertEquals(0.4, pricingDataModel.getRhCFValue(), 0.01);
        Assert.assertEquals(-0.3, pricingDataModel.getRhOSValue(), 0);
        Assert.assertEquals(pricingDataModel.getRhVirtDiscountPercentage(), pricingDataModel.getRhVirtValue(), 0);

        // SourceCost
        SourceCostsModel sourceCostsModel = report.getSourceCostsModel();

        Assert.assertEquals(9000, sourceCostsModel.getSourceLicenseValue(), 0);
        Assert.assertEquals(6750, sourceCostsModel.getSourceMaintenanceValue(), 0);

        Assert.assertEquals(600, sourceCostsModel.getYear1Server().intValue());
        Assert.assertEquals(720, sourceCostsModel.getYear2Server().intValue());
        Assert.assertEquals(864, sourceCostsModel.getYear3Server().intValue());

        Assert.assertEquals(1800000, sourceCostsModel.getYear1SourceValue(), 0);
        Assert.assertEquals(2160000, sourceCostsModel.getYear2SourceValue(), 0);
        Assert.assertEquals(2592000, sourceCostsModel.getYear3SourceValue(), 0);
        Assert.assertEquals(6552000, sourceCostsModel.getTotSourceValue(), 0);

        Assert.assertEquals(4500000, sourceCostsModel.getYear1SourceMaintenanceValue(), 0);
        Assert.assertEquals(5400000, sourceCostsModel.getYear2SourceMaintenanceValue(), 0);
        Assert.assertEquals(6480000, sourceCostsModel.getYear3SourceMaintenanceValue(), 0);
        Assert.assertEquals(16380000, sourceCostsModel.getTotSourceMaintenanceValue(), 0);

        Assert.assertEquals(22932000, sourceCostsModel.getTotalSourceValue(), 0);

        Assert.assertEquals(91125000, sourceCostsModel.getSourceNewHighValue(), 0);
        Assert.assertEquals(75937500, sourceCostsModel.getSourceNewLikelyValue(), 0);
        Assert.assertEquals(60750000, sourceCostsModel.getSourceNewLowValue(), 0);

        Assert.assertEquals(0, sourceCostsModel.getSourceNewELAIndicator().intValue());
        Assert.assertEquals(91125000, sourceCostsModel.getSourceRenewHighValue(), 0);
        Assert.assertEquals(75937500, sourceCostsModel.getSourceRenewLikelyValue(), 0);
        Assert.assertEquals(60750000, sourceCostsModel.getSourceRenewLowValue(), 0);

        // SourceRampDownCosts
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

        Assert.assertEquals(13500, sourceRampDownCostsModel.getYear1SourceMaintenancePerServerValue(), 0);
        Assert.assertEquals(15525, sourceRampDownCostsModel.getYear2SourceMaintenancePerServerValue(), 0);
        Assert.assertEquals(17854, sourceRampDownCostsModel.getYear3SourceMaintenancePerServerValue(), 0);

        Assert.assertEquals(6750000, sourceRampDownCostsModel.getYear1SourceMaintenanceTotalValue(), 0);
        Assert.assertEquals(7762500, sourceRampDownCostsModel.getYear2SourceMaintenanceTotalValue(), 0);
        Assert.assertEquals(8927000, sourceRampDownCostsModel.getYear3SourceMaintenanceTotalValue(), 0);

        // RHVRampUpCosts
        RHVRampUpCostsModel rhvRampUpCostsModel = report.getRhvRampUpCostsModel();
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvServers(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvServers(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvServers(), 0.01);
        Assert.assertEquals(300, rhvRampUpCostsModel.getYear1RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubs(), 0.01);
        Assert.assertEquals(400, rhvRampUpCostsModel.getYear2RhvPaidSubs(), 0.01);
        Assert.assertEquals(500, rhvRampUpCostsModel.getYear3RhvPaidSubs(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPerServerValue(), 0.01);
        Assert.assertEquals(375.0, rhvRampUpCostsModel.getYear2RhvPerServerValue(), 0.01);
        Assert.assertEquals(375.0, rhvRampUpCostsModel.getYear3RhvPerServerValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalValue(), 0.01);
        Assert.assertEquals(150000, rhvRampUpCostsModel.getYear2RhvTotalValue(), 0.01);
        Assert.assertEquals(187500, rhvRampUpCostsModel.getYear3RhvTotalValue(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear1RhvServersGrowth(), 0.01);
        Assert.assertEquals(220, rhvRampUpCostsModel.getYear2RhvServersGrowth(), 0.01);
        Assert.assertEquals(364, rhvRampUpCostsModel.getYear3RhvServersGrowth(), 0.01);
        Assert.assertEquals(100, rhvRampUpCostsModel.getYear1RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear2RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear3RhvCompSubsGrowth(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(220, rhvRampUpCostsModel.getYear2RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(364, rhvRampUpCostsModel.getYear3RhvPaidSubsGrowth(), 0.01);
        Assert.assertEquals(375.0, rhvRampUpCostsModel.getYear1RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(375.0, rhvRampUpCostsModel.getYear2RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(375.0, rhvRampUpCostsModel.getYear3RhvPerServerGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(82500.0, rhvRampUpCostsModel.getYear2RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(136500.0, rhvRampUpCostsModel.getYear3RhvTotalGrowthValue(), 0.01);
        Assert.assertEquals(0, rhvRampUpCostsModel.getYear1RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(232500.0, rhvRampUpCostsModel.getYear2RhvGrandTotalGrowthValue(), 0.01);
        Assert.assertEquals(324000.0, rhvRampUpCostsModel.getYear3RhvGrandTotalGrowthValue(), 0.01);


        Assert.assertEquals(30000, rhvRampUpCostsModel.getRhvSwitchLearningSubsValue(), 0.01);
        Assert.assertEquals(100000, rhvRampUpCostsModel.getRhvSwitchConsultValue(), 0.01);
        Assert.assertEquals(30000, rhvRampUpCostsModel.getRhvSwitchTAndEValue(), 0.01);

        // RHVYearByYearCosts
        RHVYearByYearCostsModel rhvYearByYearCostsModel = report.getRhvYearByYearCostsModel();
        Assert.assertEquals(6910000.0, rhvYearByYearCostsModel.getYear1RhvGrandTotalValue(), 0);
        Assert.assertEquals(7995000.0, rhvYearByYearCostsModel.getYear2RhvGrandTotalValue(), 0);
        Assert.assertEquals(9251000.0, rhvYearByYearCostsModel.getYear3RhvGrandTotalValue(), 0);

        Assert.assertEquals(23465000.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(18402500.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(13340000.0, rhvYearByYearCostsModel.getYear1RhvBudgetFreedLowValue(), 0);

        Assert.assertEquals(22380000.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(17317500.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(12255000.0, rhvYearByYearCostsModel.getYear2RhvBudgetFreedLowValue(), 0);

        Assert.assertEquals(21124000.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedHighValue(), 0);
        Assert.assertEquals(16061500.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedLikelyValue(), 0);
        Assert.assertEquals(10999000.0, rhvYearByYearCostsModel.getYear3RhvBudgetFreedLowValue(), 0);

        // RHVSavings
        RHVSavingsModel savingsModel = report.getRhvSavingsModel();

        Assert.assertEquals(66969000, savingsModel.getRhvSaveHighValue(), 0);
        Assert.assertEquals(51781500, savingsModel.getRhvSaveLikelyValue(), 0);
        Assert.assertEquals(36594000, savingsModel.getRhvSaveLowValue(), 0);

        // RHVAdditionalContainerCapacity

        // RHVOrderForm
    }

    @Test @Ignore
    public void test_SourceNewELAIndicator_1()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules", 23);

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

    @Test @Ignore
    public void test_SourceNewELAIndicator_2()
    {
        // check that the numbers of rule from the DRL file is the number of rules loaded
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules", 23);

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
