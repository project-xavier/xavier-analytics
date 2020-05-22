package org.jboss.xavier.analytics.functions;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class HelperFunctions
{
    public static boolean valueMatchesAll(String value, String... match)
    {
        String descriptionLowerCase = value.toLowerCase();
        return Stream.of(match).map(String::toLowerCase)
                .allMatch(descriptionLowerCase::contains);
    }

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

    /*
    functionally, isUnsupported is the absence of any of the other OS categorizations being true
    so we check they are all false to return true for this method
     */
    public static boolean isUnsupportedOS(String osToCheck)
    {
        return !isUndetectedOS(osToCheck) && !isSupportedOS(osToCheck) && !isConvertibleOS(osToCheck);
    }

    public static boolean isUndetectedOS(String osToCheck)
    {
        return osToCheck == null || osToCheck.trim().isEmpty() || osToCheck.equals(WorkloadInventoryReportModel.OS_NAME_DEFAULT_VALUE);
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

    public enum FlagUnsuitabilityForTargets{
        RDM_DISK(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME, true, false),
        SHARED_DISK(WorkloadInventoryReportModel.SHARED_DISK_FLAG_NAME,true, true),
        CPU_MEMORY_HOTPLUG(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME, true, true),
        CPU_AFFINITY(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME, false, true);

        private final String name;
        private final boolean isUnsuitableForOSP;
        private final boolean isUnsuitableforOCP;

        FlagUnsuitabilityForTargets(String name, boolean isUnsuitableForOSP, boolean isUnsuitableForOCP)
        {
            this.name = name;
            this.isUnsuitableForOSP = isUnsuitableForOSP;
            this.isUnsuitableforOCP = isUnsuitableForOCP;
        }

        boolean isUnsuitableForOSP()
        {
            return this.isUnsuitableForOSP;
        }

        boolean isUnsuitableForOCP()
        {
            return this.isUnsuitableforOCP;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public static boolean isFlagUnsuitableForOSP(String flagToCheck)
    {
        return Arrays.stream(FlagUnsuitabilityForTargets.values()).anyMatch(value -> flagToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isUnsuitableForOSP());
    }

    public static boolean isFlagUnsuitableForOCP(String flagToCheck)
    {
        return Arrays.stream(FlagUnsuitabilityForTargets.values()).anyMatch(value -> flagToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isUnsuitableForOCP());
    }
}
