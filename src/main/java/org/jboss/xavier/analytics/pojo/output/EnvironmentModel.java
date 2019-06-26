package org.jboss.xavier.analytics.pojo.output;

import org.kie.api.definition.type.Label;

public class EnvironmentModel
{
    // Environment
    private Integer sourceProductIndicator;
    @Label("Number of hypervisors found")
    private Integer hypervisors;
    private Integer year1Hypervisor;
    private Integer year2Hypervisor;
    private Integer year3Hypervisor;
    private Double growthRatePercentage;

    public EnvironmentModel() {}

    public Integer getSourceProductIndicator() {
        return sourceProductIndicator;
    }

    public void setSourceProductIndicator(Integer sourceProductIndicator) {
        this.sourceProductIndicator = sourceProductIndicator;
    }

    public Integer getHypervisors() {
        return hypervisors;
    }

    public void setHypervisors(Integer hypervisors) {
        this.hypervisors = hypervisors;
    }

    public Integer getYear1Hypervisor() {
        return year1Hypervisor;
    }

    public void setYear1Hypervisor(Integer year1Hypervisor) {
        this.year1Hypervisor = year1Hypervisor;
    }

    public Integer getYear2Hypervisor() {
        return year2Hypervisor;
    }

    public void setYear2Hypervisor(Integer year2Hypervisor) {
        this.year2Hypervisor = year2Hypervisor;
    }

    public Integer getYear3Hypervisor() {
        return year3Hypervisor;
    }

    public void setYear3Hypervisor(Integer year3Hypervisor) {
        this.year3Hypervisor = year3Hypervisor;
    }

    public Double getGrowthRatePercentage() {
        return growthRatePercentage;
    }

    public void setGrowthRatePercentage(Double growthRatePercentage) {
        this.growthRatePercentage = growthRatePercentage;
    }
}
