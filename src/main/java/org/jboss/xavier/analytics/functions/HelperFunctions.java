package org.jboss.xavier.analytics.functions;

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

    public static boolean isUnsupportedOS(String osToCheck)
    {
        return Arrays.stream(OSSupport.values()).anyMatch(value -> osToCheck.toLowerCase().contains(value.getName().toLowerCase()) && !value.isSupported());
    }

    public static boolean isUndetectedOS(String osToCheck)
    {
        return Arrays.stream(OSSupport.values()).noneMatch(value -> osToCheck.toLowerCase().contains(value.getName().toLowerCase()));
    }

    public enum OSSupport{
        RHEL("Red Hat Enterprise Linux", true),
        SUSE("SUSE Linux Enterprise Server", true),
        WINDOWS("Windows",true),
        ORACLE("Oracle Enterprise Linux",false),
        CENTOS("CentOS",false),
        DEBIAN("Debian",false),
        UBUNTU("Ubuntu",false);

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
}
