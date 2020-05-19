package org.jboss.xavier.analytics.functions;

import org.jboss.xavier.analytics.pojo.output.workload.inventory.WorkloadInventoryReportModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HelperFunctionsTest {
    @Parameterized.Parameters(name = "{index}: Test OS name {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Red Hat Enterprise Linux 5 (64-bit)", true, false, false, false},
                {"Red Hat Enterprise Linux 6 (64-bit)", true, false, false, false},
                {"Red Hat Enterprise Linux 7 (64-bit)", true, false, false, false},
                {"Red Hat Enterprise Linux 7.6", true, false, false, false},
                {"suse", true, false, false, false},
                {"SUSE Linux Enterprise 12 (64-bit)", true, false, false, false},
                {"windOWS", true, false, false, false},
                {"Microsoft Windows Server 2003 Standard (32-bit)", true, false, false, false},
                {"Microsoft Windows Server 2008 R2 (64-bit)", true, false, false, false},
                {"Microsoft Windows 7 (64-bit)", true, false, false, false},
                {"Oracle Linux", false, true, false, false},
                {"Oracle Linux 4/5 or later (64-bit)", false, true, false, false},
                {"Oracle Linux 6 (64-bit)", false, true, false, false},
                {"Oracle Solaris 10 (64-bit)", false, false, true, false},
                {"centos", false, true, false, false},
                {"CentOS 4/5 or later (32-bit)", false, true, false, false},
                {"CentOS 4/5 or later (64-bit)", false, true, false, false},
                {"CentOS 7 (64-bit)", false, true, false, false},
                {"CentOS 8 (64-bit)", false, true, false, false},
                {"windOWS xp", false, false, true, false},
                {"Microsoft Windows XP Professional (32-bit)", false, false, true, false},
                {"Enterprise", false, false, true, false},
                {"", false, false, false, true},
                {WorkloadInventoryReportModel.OS_NAME_DEFAULT_VALUE, false, false, false, true},
        });
    }

    @Parameterized.Parameter
    public String osName;

    @Parameterized.Parameter(1)
    public boolean isSupported;

    @Parameterized.Parameter(2)
    public boolean isConvertible;

    @Parameterized.Parameter(3)
    public boolean isUnsupported;

    @Parameterized.Parameter(4)
    public boolean isUndetected;

    @Test
    public void isSupportedOSTest() {
        Assert.assertEquals(isSupported, HelperFunctions.isSupportedOS(osName));
    }

    @Test
    public void isConvertibleOSTest() {
        Assert.assertEquals(isConvertible, HelperFunctions.isConvertibleOS(osName));
    }

    @Test
    public void isUnsupportedOSTest() {
        Assert.assertEquals(isUnsupported, HelperFunctions.isUnsupportedOS(osName));
    }

    @Test
    public void isUndetectedOSTest() {
        Assert.assertEquals(isUndetected, HelperFunctions.isUndetectedOS(osName));
    }

}
