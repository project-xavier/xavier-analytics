package org.jboss.xavier.analytics.test;

import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.spi.AgendaGroup;
import org.junit.Assert;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Utils
{
    public static FireAllRulesCommand newFireAllRulesCommand(final String agendaGroupFilter)
    {
        FireAllRulesCommand fireAllRulesCommand = new FireAllRulesCommand();
        fireAllRulesCommand.setAgendaFilter(match -> {
            String ruleAgendaGroup = ((RuleTerminalNodeLeftTuple) match).getAgendaGroup().getName();
            System.out.printf("Rule has agenda group '%s' and the test executes only the '%s' agenda group or the default '%s' agenda group\n", ruleAgendaGroup, agendaGroupFilter, AgendaGroup.MAIN);
            return agendaGroupFilter.equals(ruleAgendaGroup) || AgendaGroup.MAIN.equals(ruleAgendaGroup);
        });
        return fireAllRulesCommand;
    }

    public static List<Command> newInsertCommands(Map<String, Object> facts)
    {
        List<Command> commands = new ArrayList<>();
        for (Map.Entry<String, Object> entry : facts.entrySet()) {
            Command insertFactCommand = CommandFactory.newInsert(entry.getValue(), entry.getKey());
            commands.add(insertFactCommand);
        }
        return commands;
    }

    public static Map<String, Object> executeCommandsAndGetResults(CommandExecutor kieSession, List<Command> commands)
    {
        ExecutionResults executionResults = null;
        try {
            executionResults = kieSession.execute(CommandFactory.newBatchExecution(commands));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> results = new HashMap(executionResults.getIdentifiers().size());
        for (String identifier : executionResults.getIdentifiers()) {
            results.put(identifier, executionResults.getValue(identifier));
        }
        return results;
    }

    public static <T> List<T> extractModels(String objectKeyInResults, Map<String, Object> results, Class<T> model)
    {
        final List<Object> objects = (List<Object>) results.get(objectKeyInResults);
        return objects.stream()
                .filter(model::isInstance)
                .map(model::cast)
                .collect(Collectors.toList());
    }

    public static void verifyRulesFiredNames(AgendaEventListener agendaEventListener, String ... rulesNames)
    {
        int numberOfRules = rulesNames.length;
        // create an argument captor for AfterActivationFiredEvent
        ArgumentCaptor<AfterMatchFiredEvent> argumentCaptor = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        // check that the method was called for #numberOfRules times and capture the arguments
        verify( agendaEventListener, times(numberOfRules) ).afterMatchFired(argumentCaptor.capture());
        List<AfterMatchFiredEvent> events = argumentCaptor.getAllValues();

        Assert.assertEquals(events.size(), rulesNames.length);
        Assert.assertTrue(Arrays.asList(rulesNames).stream().anyMatch(a -> events.stream().anyMatch(e -> e.getMatch().getRule().getName().equalsIgnoreCase(a))));
    }

    public static void checkLoadedRulesNumber(StatelessKieSession kieSession, String kiePackageName, int expectedLoadedRules)
    {
        int actualLoadedRules;
        if (kiePackageName.endsWith(".*"))
        {
            final String baseKiePackageName = kiePackageName.substring(0, kiePackageName.length() - 2);
            Collection<KiePackage> kiePackages = kieSession.getKieBase().getKiePackages();
            Assert.assertNotNull("No packages have been found in kieSession", kiePackages);
            Assert.assertFalse("No rules have been loaded from '" + kiePackageName + "' package", kiePackages.isEmpty());
            AtomicInteger totalNumberOfRules = new AtomicInteger(0);
            kiePackages.stream()
                .filter(kiePackage -> kiePackage.getName().startsWith(baseKiePackageName))
                .forEach(kiePackage -> totalNumberOfRules.addAndGet(kiePackage.getRules().size()));
            actualLoadedRules = totalNumberOfRules.get();
        }
        else
        {
            KiePackage kiePackage = kieSession.getKieBase().getKiePackage(kiePackageName);
            Assert.assertNotNull("No rules have been loaded from '" + kiePackageName + "' package", kiePackage);
            actualLoadedRules = kiePackage.getRules().size();
        }
        Assert.assertEquals("Wrong number of rules loaded", expectedLoadedRules, actualLoadedRules);
    }
}
