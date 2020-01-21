package org.jboss.xavier.analytics.rules.initialcostsaving;

import org.jboss.xavier.analytics.pojo.input.UploadFormInputDataModel;
import org.jboss.xavier.analytics.pojo.output.EnvironmentModel;
import org.jboss.xavier.analytics.pojo.output.InitialSavingsEstimationReportModel;
import org.jboss.xavier.analytics.rules.BaseTest;
import org.jboss.xavier.analytics.test.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentTest extends BaseTest {
    private static final String CUSTOMER_ID = "123";
    private static final Integer NUMBER_OF_HYPERVISORS = 101;
    private static final double GROWTH_RATE_PERCENTAGE = 0.05;
    private static final String FILE_NAME = "example_payload.zip";
    private static final int DEFAULT_SOURCE_PRODUCT_INDICATOR = 1;

    public EnvironmentTest() {
        super("/org/jboss/xavier/analytics/rules/initialcostsaving/Environment.drl", ResourceType.DRL,
                "org.jboss.xavier.analytics.rules.initialcostsaving", 1);
    }

    @Test
    public void test() {
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

        Map<String, Object> results = createAndExecuteCommandsAndGetResults(facts);

        Assert.assertEquals(1, results.get(NUMBER_OF_FIRED_RULE_KEY));
        Utils.verifyRulesFiredNames(this.agendaEventListener, "Copy input fields and agenda controller");

        List<InitialSavingsEstimationReportModel> reports = Utils.extractModels(GET_OBJECTS_KEY, results, InitialSavingsEstimationReportModel.class);

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
