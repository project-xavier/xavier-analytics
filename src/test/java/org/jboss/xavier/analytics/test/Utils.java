package org.jboss.xavier.analytics.test;

import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.spi.AgendaGroup;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ExecutionResults executionResults = kieSession.execute(CommandFactory.newBatchExecution(commands));
        Map<String, Object> results = new HashMap(executionResults.getIdentifiers().size());
        for (String identifier : executionResults.getIdentifiers()) {
            results.put(identifier, executionResults.getValue(identifier));
        }
        return results;
    }

    public static <T> List<T> getListOf(Class<T> t, Map<String, Object> results, String objectKey)
    {
        List<Object> objects = (List<Object>) results.get((objectKey));
        List<T> tList = objects.stream()
                .filter(object -> t.isAssignableFrom(object.getClass()))
                .map(object -> (T) object)
                .collect(Collectors.toList());
        return tList;
    }

}
