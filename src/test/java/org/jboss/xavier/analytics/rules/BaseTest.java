package org.jboss.xavier.analytics.rules;

import org.jboss.xavier.analytics.test.Utils;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public abstract class BaseTest {
    protected static final String GET_OBJECTS_KEY = "_getObjects";
    protected static final String NUMBER_OF_FIRED_RULE_KEY = "numberOfFiredRules";

    protected final String rulePath;
    protected final ResourceType ruleResourceType;
    protected final String expectedKiePackageName;
    protected final int expectedLoadedRules;

    protected StatelessKieSession kieSession;
    protected KieFileSystem kieFileSystem;
    protected KieServices kieServices;

    protected AgendaEventListener agendaEventListener;

    public BaseTest(String rulePath, ResourceType resourceType, String expectedKiePackageName, int expectedLoadedRules)
    {
        this.rulePath = rulePath;
        this.ruleResourceType = resourceType;
        // AgendaEventListeners allow one to monitor and check rules that activate, fire, etc
        agendaEventListener = mock( AgendaEventListener.class );
        this.expectedKiePackageName = expectedKiePackageName;
        this.expectedLoadedRules = expectedLoadedRules;
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

    // check that the number of rule (from the DRL files) is the number of rules loaded
    @Test
    public void checkLoadedRulesNumber()
    {
        Utils.checkLoadedRulesNumber(kieSession, expectedKiePackageName, expectedLoadedRules);
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

    public Map<String, Object> createAndExecuteCommandsAndGetResults(final Map<String, Object> facts)
    {
        final List<Command> commands = new ArrayList<>();
        commands.addAll(Utils.newInsertCommands(facts));
        commands.add(CommandFactory.newFireAllRules(NUMBER_OF_FIRED_RULE_KEY));
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));
        return Utils.executeCommandsAndGetResults(kieSession, commands);
    }
}
