package org.jboss.xavier.analytics.functions;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class HelperFunctions
{
    public static int round(double value)
    {
        return (int) Math.round(value);
    }

    public static boolean isSupportedOS(String osToCheck)
    {
        return OSSupport.findOSSupportForOS(osToCheck)
                .map(OSSupport::isSupported)
                .orElse(false);
    }

    public static boolean isConvertibleOS(String osToCheck)
    {
        return OSSupport.findOSSupportForOS(osToCheck)
                .map(OSSupport::isConvertible)
                .orElse(false);
    }

    public static boolean isUnsupportedOS(String osToCheck)
    {
        return !isUndetectedOS(osToCheck) && !isSupportedOS(osToCheck) && !isConvertibleOS(osToCheck);
    }

    public static boolean isUndetectedOS(String osToCheck)
    {
        return osToCheck == null || osToCheck.trim().isEmpty();
    }

    public enum OSSupport
    {
        RHEL("Red Hat Enterprise Linux", true, false),
        SUSE("SUSE", true, false),
        WINDOWS("Windows",true, false),
        ORACLE("Oracle Linux",false, true),
        CENTOS("CentOS",false, true),
        WINDOWS_XP("Windows XP", false, false);

        private final String name;
        private final boolean isSupported;
        private final boolean isConvertible;

        OSSupport(String name, boolean isSupported, boolean isConvertible)
        {
            this.name = name;
            this.isSupported = isSupported;
            this.isConvertible = isConvertible;
        }

        boolean isSupported()
        {
            return this.isSupported;
        }

        public String getName()
        {
            return this.name;
        }

        boolean isConvertible()
        {
            return this.isConvertible;
        }

        public static Optional<OSSupport> findOSSupportForOS(String osName)
        {
            return Arrays.stream(OSSupport.values())
                    .filter(os -> osName.toLowerCase().contains(os.getName().toLowerCase()))
                    // then find the longest matched OSSupport name and return OSSupport as result
                    // Example: "Microsoft Windows XP Professional" would match both "Windows" and "Windows XP" but the latter is the best match
                    .max(Comparator.comparingInt(os -> os.getName().length()));
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
