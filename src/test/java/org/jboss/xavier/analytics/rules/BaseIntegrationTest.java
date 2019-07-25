package org.jboss.xavier.analytics.rules;

import org.junit.After;
import org.junit.Before;
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

    public BaseIntegrationTest(String kSessionName)
    {
        this.kSessionName = kSessionName;
        // AgendaEventListeners allow one to monitor and check rules that activate, fire, etc
        agendaEventListener = mock( AgendaEventListener.class );
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

    @After
    public void tearDown()
    {
    }
}
