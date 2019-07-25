package org.jboss.xavier.analytics.pojo.output;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AnalysisModel
{
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "AnalysisModel_ID_GENERATOR")
    private Long id;

    @OneToOne(mappedBy = "analysis", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private InitialSavingsEstimationReportModel initialSavingsEstimationReportModel;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkloadInventoryReportModel> workloadInventoryReportModels;

    private String reportName;
    private String reportDescription;
    private String payloadName;
    private String status;

    public AnalysisModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InitialSavingsEstimationReportModel getInitialSavingsEstimationReportModel() {
        return initialSavingsEstimationReportModel;
    }

    public void setInitialSavingsEstimationReportModel(InitialSavingsEstimationReportModel initialSavingsEstimationReportModel) {
        this.initialSavingsEstimationReportModel = initialSavingsEstimationReportModel;
        initialSavingsEstimationReportModel.setAnalysis(this);
    }

    public List<WorkloadInventoryReportModel> getWorkloadInventoryReportModels() {
        return workloadInventoryReportModels;
    }

    public void setWorkloadInventoryReportModels(List<WorkloadInventoryReportModel> workloadInventoryReportModels) {
        this.workloadInventoryReportModels = workloadInventoryReportModels;
        workloadInventoryReportModels.forEach(workloadInventoryReportModel -> workloadInventoryReportModel.setAnalysis(this));
    }

    public void addWorkloadInventoryReportModel(WorkloadInventoryReportModel workloadInventoryReportModel)
    {
        if (this.workloadInventoryReportModels == null) this.workloadInventoryReportModels = new ArrayList<>();
        this.workloadInventoryReportModels.add(workloadInventoryReportModel);
        workloadInventoryReportModel.setAnalysis(this);
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public void setPayloadName(String payloadName) {
        this.payloadName = payloadName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
