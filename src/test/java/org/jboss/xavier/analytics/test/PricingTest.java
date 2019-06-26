package org.jboss.xavier.analytics.test;

import org.jboss.xavier.analytics.pojo.output.EnvironmentModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.pojo.support.PricingDataModel;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.builder.KieBuilder;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.internal.command.CommandFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PricingTest extends BaseTest
{
    private static final int DEFAULT_SOURCE_PRODUCT_INDICATOR = 1;

    public PricingTest()
    {
        super("/org/jboss/xavier/analytics/rules/PricingRule.xlsx", ResourceType.DTABLE);
    }

    @Override
    protected KieBuilder createAndBuildKieBuilder(URL resource)
    {
        FileInputStream ruleFile = null;
        try {
            ruleFile = new FileInputStream(resource.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        kieFileSystem.write("src/main/resources/" + this.rulePath, kieServices.getResources().newInputStreamResource(ruleFile).setResourceType(this.ruleResourceType));

/*
        Resource dt = ResourceFactory.newClassPathResource(this.rulePath, getClass());
        DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
        DecisionTableConfiguration decisionTableConfiguration = new DecisionTableConfigurationImpl();
        decisionTableConfiguration.setInputType(DecisionTableInputType.XLSX);
        String drl = decisionTableProvider.loadFromResource(dt, decisionTableConfiguration);
        kieFileSystem.write("src/main/resources/org/jboss/xavier/analytics/rules/PricingRule.drl", drl);
*/

        addAgendaGroupRuleToKieFileSystem(kieFileSystem);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        return kieBuilder;
    }

    @Test
    public void test()
    {
        checkLoadedRulesNumber(4);

        EnvironmentModel environmentModel = new EnvironmentModel();
        environmentModel.setSourceProductIndicator(DEFAULT_SOURCE_PRODUCT_INDICATOR);
        InitialSavingsEstimationReportModel reportModel = new InitialSavingsEstimationReportModel();
        reportModel.setEnvironmentModel(environmentModel);

        Map<String, Object> facts = new HashMap<>();
        facts.put("reportModel", reportModel);
        facts.put("agendaGroup", "Pricing");

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(4, results.get(NUMBER_OF_FIRED_RULE_KEY));

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));

        List<InitialSavingsEstimationReportModel> reports = objects.stream()
                .filter(object -> object instanceof InitialSavingsEstimationReportModel)
                .map(object -> (InitialSavingsEstimationReportModel) object)
                .collect(Collectors.toList());

        // just one InitialSavingsEstimationReportModel has to be available
        Assert.assertEquals(1, reports.size());

        List<PricingDataModel> pricingDataModelList = objects.stream()
                .filter(object -> object instanceof PricingDataModel)
                .map(object -> (PricingDataModel) object)
                .collect(Collectors.toList());

        // just one PricingDataModel has to be created
        Assert.assertEquals(1, pricingDataModelList.size());
        PricingDataModel pricingDataModel = pricingDataModelList.get(0);
        Assert.assertEquals(10000, pricingDataModel.getSourceListValue(), 0);
        Assert.assertEquals(0.1, pricingDataModel.getSourceDiscountPercentage(), 0);
        Assert.assertEquals(0.75, pricingDataModel.getSourceMaintenancePercentage(), 0);
        Assert.assertEquals(3.5, pricingDataModel.getSourceRenewHighFactor(), 0);
        Assert.assertEquals(2.75, pricingDataModel.getSourceRenewLikelyFactor(), 0);
        Assert.assertEquals(2.0, pricingDataModel.getSourceRenewLowFactor(), 0);
    }
}
