package org.jboss.xavier.analytics.pojo.support;

/*
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
*/
import org.kie.api.definition.type.Label;

/*@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor*/
public class PricingDataModel implements java.io.Serializable {

    @Label("Source product list price value")
    private Double sourceListValue;

    @Label("Source product discount percentage (0-1)")
    private Double sourceDiscountPercentage;

    @Label("Source product maintenance price percentage (0-1)")
    private Double sourceMaintenancePercentage;

    @Label("Source product renewal goal high")
    private Double sourceRenewHighFactor;

    @Label("Source product renewal goal likely")
    private Double sourceRenewLikelyFactor;

    @Label("Source product renewal goal low")
    private Double sourceRenewLowFactor;

    @Label("Year-over-Year growth in maintenance cost due to breaking ELA")
    private Double sourceMaintenanceGrowthPercentage;

    @Label("RHV or RHV Suite Consulting")
    private Double rhvConsultValue;

    @Label("RHV or RHV Suite T&E for consulting")
    private Double rhvTAndEValue;

    @Label("Red Hat Learning subscriptions")
    private Double rhLearningSubsValue;

    @Label("Red Hat Virtualization, Premium")
    private Double rhvListValue;

    @Label("Red Hat Virtualization, Premium Discount")
    private Double rhvDiscountperc;

    @Label("Red Hat CloudForms, Premium")
    private Double rhCFListValue;

    @Label("Red Hat CloudForms, Premium Discount")
    private Double rhCFDiscountPercentage;

    @Label("Red Hat OpenShift, Premium")
    private Double rhOSListValue;

    @Label("Red Hat OpenShift, Premium Discount")
    private Double rhOSDiscountPercentage;

    @Label("Red Hat Virtualization Suite, Premium")
    private Double rhVirtListValue;

    @Label("Red Hat Virtualization Suite, Premium Discount")
    private Double rhVirtDiscountPercentage;

    @Label("Red Hat Generosity")
    private Double freeSubsYear1Indicator;

    @Label("Red Hat Generosity")
    private Double freeSubsYear2And3Indicator;

    public PricingDataModel() {}

    public Double getSourceListValue() {
        return sourceListValue;
    }

    public void setSourceListValue(Double sourceListValue) {
        this.sourceListValue = sourceListValue;
    }

    public Double getSourceDiscountPercentage() {
        return sourceDiscountPercentage;
    }

    public void setSourceDiscountPercentage(Double sourceDiscountPercentage) {
        this.sourceDiscountPercentage = sourceDiscountPercentage;
    }

    public Double getSourceMaintenancePercentage() {
        return sourceMaintenancePercentage;
    }

    public void setSourceMaintenancePercentage(Double sourceMaintenancePercentage) {
        this.sourceMaintenancePercentage = sourceMaintenancePercentage;
    }

    public Double getSourceRenewHighFactor() {
        return sourceRenewHighFactor;
    }

    public void setSourceRenewHighFactor(Double sourceRenewHighFactor) {
        this.sourceRenewHighFactor = sourceRenewHighFactor;
    }

    public Double getSourceRenewLikelyFactor() {
        return sourceRenewLikelyFactor;
    }

    public void setSourceRenewLikelyFactor(Double sourceRenewLikelyFactor) {
        this.sourceRenewLikelyFactor = sourceRenewLikelyFactor;
    }

    public Double getSourceRenewLowFactor() {
        return sourceRenewLowFactor;
    }

    public void setSourceRenewLowFactor(Double sourceRenewLowFactor) {
        this.sourceRenewLowFactor = sourceRenewLowFactor;
    }

    public Double getSourceMaintenanceGrowthPercentage() {
        return sourceMaintenanceGrowthPercentage;
    }

    public void setSourceMaintenanceGrowthPercentage(Double sourceMaintenanceGrowthPercentage) {
        this.sourceMaintenanceGrowthPercentage = sourceMaintenanceGrowthPercentage;
    }

    public Double getRhvConsultValue() {
        return rhvConsultValue;
    }

    public void setRhvConsultValue(Double rhvConsultValue) {
        this.rhvConsultValue = rhvConsultValue;
    }

    public Double getRhvTAndEValue() {
        return rhvTAndEValue;
    }

    public void setRhvTAndEValue(Double rhvTAndEValue) {
        this.rhvTAndEValue = rhvTAndEValue;
    }

    public Double getRhLearningSubsValue() {
        return rhLearningSubsValue;
    }

    public void setRhLearningSubsValue(Double rhLearningSubsValue) {
        this.rhLearningSubsValue = rhLearningSubsValue;
    }

    public Double getRhvListValue() {
        return rhvListValue;
    }

    public void setRhvListValue(Double rhvListValue) {
        this.rhvListValue = rhvListValue;
    }

    public Double getRhvDiscountperc() {
        return rhvDiscountperc;
    }

    public void setRhvDiscountperc(Double rhvDiscountperc) {
        this.rhvDiscountperc = rhvDiscountperc;
    }

    public Double getRhCFListValue() {
        return rhCFListValue;
    }

    public void setRhCFListValue(Double rhCFListValue) {
        this.rhCFListValue = rhCFListValue;
    }

    public Double getRhCFDiscountPercentage() {
        return rhCFDiscountPercentage;
    }

    public void setRhCFDiscountPercentage(Double rhCFDiscountPercentage) {
        this.rhCFDiscountPercentage = rhCFDiscountPercentage;
    }

    public Double getRhOSListValue() {
        return rhOSListValue;
    }

    public void setRhOSListValue(Double rhOSListValue) {
        this.rhOSListValue = rhOSListValue;
    }

    public Double getRhOSDiscountPercentage() {
        return rhOSDiscountPercentage;
    }

    public void setRhOSDiscountPercentage(Double rhOSDiscountPercentage) {
        this.rhOSDiscountPercentage = rhOSDiscountPercentage;
    }

    public Double getRhVirtListValue() {
        return rhVirtListValue;
    }

    public void setRhVirtListValue(Double rhVirtListValue) {
        this.rhVirtListValue = rhVirtListValue;
    }

    public Double getRhVirtDiscountPercentage() {
        return rhVirtDiscountPercentage;
    }

    public void setRhVirtDiscountPercentage(Double rhVirtDiscountPercentage) {
        this.rhVirtDiscountPercentage = rhVirtDiscountPercentage;
    }

    public Double getFreeSubsYear1Indicator() {
        return freeSubsYear1Indicator;
    }

    public void setFreeSubsYear1Indicator(Double freeSubsYear1Indicator) {
        this.freeSubsYear1Indicator = freeSubsYear1Indicator;
    }

    public Double getFreeSubsYear2And3Indicator() {
        return freeSubsYear2And3Indicator;
    }

    public void setFreeSubsYear2And3Indicator(Double freeSubsYear2And3Indicator) {
        this.freeSubsYear2And3Indicator = freeSubsYear2And3Indicator;
    }
}
