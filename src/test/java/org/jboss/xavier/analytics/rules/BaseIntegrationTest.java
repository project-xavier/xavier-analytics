package org.jboss.xavier.analytics.rules;

import org.jboss.xavier.analytics.test.Utils;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.StatelessKieSession;

import static org.mockito.Mockito.mock;

public abstract class BaseIntegrationTest {
    protected static final String QUERY_IDENTIFIER = "_queryIdentifier";
    protected static final String GET_OBJECTS_KEY = "_getObjects";
    protected static final String NUMBER_OF_FIRED_RULE_KEY = "numberOfFiredRules";

    protected final String kSessionName;

    protected StatelessKieSession kieSession;
    protected KieServices kieServices;

    protected AgendaEventListener agendaEventListener;

    protected final String expectedKiePackageName;
    protected final int expectedLoadedRules;


    public BaseIntegrationTest(String kSessionName, String expectedKiePackageName, int expectedLoadedRules)
    {
        this.kSessionName = kSessionName;
        // AgendaEventListeners allow one to monitor and check rules that activate, fire, etc
        agendaEventListener = mock( AgendaEventListener.class );
        this.expectedKiePackageName = expectedKiePackageName;
        this.expectedLoadedRules = expectedLoadedRules;
    }

    @Before
    public void setup()
    {
        kieServices = KieServices.Factory.get();
        kieSession = kieServices.getKieClasspathContainer().newStatelessKieSession(kSessionName);
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        kieSession.addEventListener(new DebugAgendaEventListener());
        kieSession.addEventListener(agendaEventListener);
    }

    // check that the number of rule (from the DRL files) is the number of rules loaded
    @Test
    public void checkLoadedRulesNumber()
    {
        Utils.checkLoadedRulesNumber(kieSession, expectedKiePackageName, expectedLoadedRules);
    }
}
