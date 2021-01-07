package org.jboss.xavier.analytics.functions;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
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
        RDM_DISK(WorkloadInventoryReportModel.RDM_DISK_FLAG_NAME, true, false, null),
        CPU_MEMORY_HOTPLUG(WorkloadInventoryReportModel.CPU_MEMORY_HOTPLUG_FLAG_NAME, true, true, null),
        CPU_AFFINITY(WorkloadInventoryReportModel.CPU_AFFINITY_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        UEFI_BOOT(WorkloadInventoryReportModel.UEFI_BOOT_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL),
        VM_AFFINITY_CONFIG(WorkloadInventoryReportModel.VM_HOST_AFFINITY_CONFIGURED_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        NUMA_NODE_AFFINITY(WorkloadInventoryReportModel.NUMA_NODE_AFFINITY_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        VM_DRS_CONFIG(WorkloadInventoryReportModel.VM_DRS_CONFIG_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_INFORMATION),
        VM_HA_CONFIG(WorkloadInventoryReportModel.VM_HA_CONFIG_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        BALLOONED_MEMORY(WorkloadInventoryReportModel.BALLOONED_MEMORY_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        ENCRYPTED_DISK(WorkloadInventoryReportModel.ENCRYPTED_DISK_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL),
        OPAQUE_NETWORK(WorkloadInventoryReportModel.OPAQUE_NETWORK_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL),
        HAS_PASSTHROUGH_DEVICE(WorkloadInventoryReportModel.PASSTHROUGH_DEVICE_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL),
        HAS_USB_CONTROLLERS(WorkloadInventoryReportModel.USB_CONTROLLERS_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_WARNING),
        SR_IOV_NIC(WorkloadInventoryReportModel.SR_IOV_NIC_FLAG_NAME, false, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL),
        SHARED_VMDK(WorkloadInventoryReportModel.SHARED_VMDK_FLAG_NAME, true, true, WorkloadInventoryReportModel.FLAG_CATEGORY_CRITICAL);


        private final String name;
        private final boolean isUnsuitableForOSP;
        private final boolean isUnsuitableforOCP;
        private final String categoryLevel;

        FlagUnsuitabilityForTargets(String name, boolean isUnsuitableForOSP, boolean isUnsuitableForOCP, String categoryLevel)
        {
            this.name = name;
            this.isUnsuitableForOSP = isUnsuitableForOSP;
            this.isUnsuitableforOCP = isUnsuitableForOCP;
            this.categoryLevel = categoryLevel;
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

        public String getCategoryLevel() { return this.categoryLevel; }
    }

    public static boolean isFlagUnsuitableForOSP(String flagToCheck)
    {
        return Arrays.stream(FlagUnsuitabilityForTargets.values()).anyMatch(value -> flagToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isUnsuitableForOSP());
    }

    public static boolean isFlagUnsuitableForOCP(String flagToCheck)
    {
        return Arrays.stream(FlagUnsuitabilityForTargets.values()).anyMatch(value -> flagToCheck.toLowerCase().contains(value.getName().toLowerCase()) && value.isUnsuitableForOCP());
    }

    public static boolean doesFlagsCollectionContainCategory(Set<String> flags, String categoryToCheckFor) {
        if (flags.stream().anyMatch(flag -> Arrays.stream(FlagUnsuitabilityForTargets.values()).anyMatch
                (value -> flag.toLowerCase().contains(value.getName().toLowerCase()) && categoryToCheckFor.equals(value.getCategoryLevel()))))
        {
            return true;
        } else {
            return false;
        }
    }
}
