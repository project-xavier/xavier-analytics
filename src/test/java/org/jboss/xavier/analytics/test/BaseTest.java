package org.jboss.xavier.analytics.test;

import org.junit.After;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public abstract class BaseTest {
    protected static final String GET_OBJECTS_KEY = "_getObjects";
    protected static final String NUMBER_OF_FIRED_RULE_KEY = "numberOfFiredRules";

    protected final String rulePath;
    protected final ResourceType ruleResourceType;

    protected StatelessKieSession kieSession;
    protected KieFileSystem kieFileSystem;
    protected KieServices kieServices;

    protected AgendaEventListener agendaEventListener;

    public BaseTest(String rulePath, ResourceType resourceType)
    {
        this.rulePath = rulePath;
        this.ruleResourceType = resourceType;
        // AgendaEventListeners allow one to monitor and check rules that activate, fire, etc
        agendaEventListener = mock( AgendaEventListener.class );
    }

    @Before
    public void setup()
    {
        URL resource = getClass().getResource(rulePath);
        if (resource == null){
            throw new IllegalArgumentException(ruleResourceType + " file '" + rulePath + "' does not resolve to a resource");
        }

        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();

        KieBuilder kieBuilder = createAndBuildKieBuilder(resource);
        List<Message> errorMessages = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        errorMessages.forEach(System.err::println);
        assertEquals(0, errorMessages.size());

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        kieSession = kContainer.newStatelessKieSession();
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());
        kieSession.addEventListener(new DebugAgendaEventListener());
        kieSession.addEventListener(agendaEventListener);
    }

    public void checkLoadedRulesNumber(int expectedLoadedRules)
    {
        Utils.checkLoadedRulesNumber(kieSession, "org.jboss.xavier.analytics.rules", expectedLoadedRules);
    }

    protected KieBuilder createAndBuildKieBuilder(URL resource)
    {
        File ruleFile = new File(resource.getPath());
        kieFileSystem.write(ResourceFactory.newFileResource(ruleFile).setResourceType(ruleResourceType));
        addAgendaGroupRuleToKieFileSystem(kieFileSystem);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        return kieBuilder;
    }

    protected void addAgendaGroupRuleToKieFileSystem(KieFileSystem kieFileSystem)
    {
        File agendaFocusForTestFile = new File(getClass().getResource("/org/jboss/xavier/analytics/test/rules/AgendaFocusForTest.drl").getPath());
        kieFileSystem.write(ResourceFactory.newFileResource(agendaFocusForTestFile).setResourceType(ResourceType.DRL));
    }

    @After
    public void tearDown()
    {
    }
}
