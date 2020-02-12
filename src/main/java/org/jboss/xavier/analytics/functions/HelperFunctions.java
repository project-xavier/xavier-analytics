package org.jboss.xavier.analytics.functions;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

import java.util.Arrays;

public class HelperFunctions
{
    public static int round(double value)
    {
        return (int) Math.round(value);
    }

    public static boolean isSupportedOS(String osToCheck)
    {
        return Arrays.stream(OSSupport.values()).anyMatch(value -> osToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isSupported());
    }

    public static boolean isConvertibleOS(String osToCheck)
    {
        return Arrays.stream(OSSupport.values()).anyMatch(value -> osToCheck.toLowerCase().contains(value.getName().toLowerCase()) && !value.isSupported());
    }

    public static boolean isUnsupportedOS(String osToCheck)
    {
        return Arrays.stream(OSSupport.values()).noneMatch(value -> osToCheck.toLowerCase().contains(value.getName().toLowerCase()));
    }

    public static boolean isUndetectedOS(String osToCheck)
    {
        return osToCheck == null || osToCheck.trim().isEmpty();
    }

    public enum OSSupport{
        RHEL("Red Hat Enterprise Linux", true),
        SUSE("SUSE Linux Enterprise Server", true),
        WINDOWS("Windows",true),
        ORACLE("Oracle Enterprise Linux",false),
        CENTOS("CentOS",false);

        private final String name;
        private final boolean isSupported;

        OSSupport(String name, boolean isSupported)
        {
            this.name = name;
            this.isSupported = isSupported;
        }

        boolean isSupported()
        {
            return this.isSupported;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public enum FlagUnsuitabilityForOSPTarget{
        RDM_DISK(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME, true),
        TOO_MANY_NICS(WorkloadInventoryReportModel.MORE_THAN_4_NICS_FLAG_NAME, true),
        SHARED_DISK(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME,true);

        private final String name;
        private final boolean isUnsuitable;

        FlagUnsuitabilityForOSPTarget(String name, boolean isUnsuitable)
        {
            this.name = name;
            this.isUnsuitable = isUnsuitable;
        }

        boolean isUnsuitable()
        {
            return this.isUnsuitable;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public static boolean isUnsuitableFlag(String flagToCheck)
    {
        return Arrays.stream(FlagUnsuitabilityForOSPTarget.values()).anyMatch(value -> flagToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isUnsuitable());
    }
}
