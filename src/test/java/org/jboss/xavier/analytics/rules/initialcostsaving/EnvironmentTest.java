package org.jboss.xavier.analytics.rules.initialcostsaving;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.analytics.pojo.output.EnvironmentModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
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

public class EnvironmentTest extends BaseTest {
    private static final String CUSTOMER_ID = "123";
    private static final Integer NUMBER_OF_HYPERVISORS = 101;
    private static final double GROWTH_RATE_PERCENTAGE = 0.05;
    private static final String FILE_NAME = "example_payload.zip";
    private static final int DEFAULT_SOURCE_PRODUCT_INDICATOR = 1;

    public EnvironmentTest() {
        super("/org/jboss/xavier/analytics/rules/initialcostsaving/Environment.drl", ResourceType.DRL);
    }

    @Test
    public void test() {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules.initialcostsaving", 1);

        Map<String, Object> facts = new HashMap<>();

        UploadFormInputDataModel inputDataModel = new UploadFormInputDataModel();
        inputDataModel.setCustomerId(CUSTOMER_ID);
        inputDataModel.setHypervisor(NUMBER_OF_HYPERVISORS);
        inputDataModel.setGrowthRatePercentage(GROWTH_RATE_PERCENTAGE);
        inputDataModel.setYear1HypervisorPercentage(0.5);
        inputDataModel.setYear2HypervisorPercentage(0.3);
        inputDataModel.setYear3HypervisorPercentage(0.1);
        inputDataModel.setFileName(FILE_NAME);
        facts.put("inputDataModel", inputDataModel);

        List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        Map<String, Object> results = Utils.executeCommandsAndGetResults(kieSession, commands);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Copy input fields and agenda controller");

        List<Object> objects = (List<Object>) results.get((GET_OBJECTS_KEY));
        List<InitialSavingsEstimationReportModel> reports = objects.stream()
                .filter(object -> object instanceof InitialSavingsEstimationReportModel)
                .map(object -> (InitialSavingsEstimationReportModel) object)
                .collect(Collectors.toList());

        // just one report has to be created
        Assert.assertEquals(1, reports.size());

        InitialSavingsEstimationReportModel report = reports.get(0);
        Assert.assertEquals(CUSTOMER_ID, report.getCustomerId());
        Assert.assertNotNull(report.getCreationDate());
        Assert.assertEquals(FILE_NAME, report.getFileName());

        EnvironmentModel environmentModel = report.getEnvironmentModel();
        Assert.assertEquals(NUMBER_OF_HYPERVISORS, environmentModel.getHypervisors());
        Assert.assertEquals(GROWTH_RATE_PERCENTAGE, environmentModel.getGrowthRatePercentage(), 0);
        Assert.assertEquals(51, environmentModel.getYear1Hypervisor().intValue());
        Assert.assertEquals(30, environmentModel.getYear2Hypervisor().intValue());
        Assert.assertEquals(Integer.valueOf(10), environmentModel.getYear3Hypervisor());
        Assert.assertEquals(Integer.valueOf(DEFAULT_SOURCE_PRODUCT_INDICATOR), environmentModel.getSourceProductIndicator());
        Assert.assertEquals(1, environmentModel.getDealIndicator().intValue());
        Assert.assertEquals(true, environmentModel.getOpenStackIndicator());
    }
}
