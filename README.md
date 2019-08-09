[![Build Status](https://travis-ci.org/project-xavier/xavier-analytics.svg?branch=master)](https://travis-ci.org/project-xavier/xavier-analytics)
[![codecov](https://codecov.io/gh/project-xavier/xavier-analytics/branch/master/graph/badge.svg)](https://codecov.io/gh/project-xavier/xavier-analytics)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=project-xavier_xavier-analytics&metric=alert_status)](https://sonarcloud.io/dashboard?id=project-xavier_xavier-analytics)

# xavier-analytics

## How to create a new rule set for an agenda group with test
The procedure describes how to create a rule set for the agenda group `FooAgendaGroup`
1. create a copy of [SourceCosts.drl](src/main/resources/org/jboss/xavier/analytics/rules/initialcostsaving/SourceCosts.drl) with name `FooAgendaGroup.drl` within the `org.jboss.xavier.analytics.rules` package
1. in the `FooAgendaGroup.drl` file:
    1. change the `agenda-group` value to become `FooAgendaGroup`
    1. change the `rule` ID value to something like `FooAgendaGroupRules` (i.e. `rule "FooAgendaGroupRules"`)
1. create a copy of [SourceCostsTest.java](src/test/java/org/jboss/xavier/analytics/test/SourceCostsTest.java) with name `FooAgendaGroupTest.java` within the `org.jboss.xavier.analytics.test` package
    1. change the constructor to reference the `FooAgendaGroup.drl` file instead of `SourceCosts.drl` (i.e. `super("/org/jboss/xavier/analytics/rules/FooAgendaGroup.drl", ResourceType.DRL);`)
    1. change the `agendaGroup` to reference the `FooAgendaGroup` agenda group (i.e. `facts.put("agendaGroup", "FooAgendaGroup");`)
1. run the `mvn -gs ./configuration/settings.xml test -Dtest=FooAgendaGroupTest` to check that everything works fine

Now you have a working rule set with a test and you can start developing the rules keeping updated the test.

## How to interact locally with OKD
1. Follow the "[Installation](https://github.com/project-xavier/xavier-integration#installation)" procedure from [xavier-integration](https://github.com/project-xavier/xavier-integration) project with one difference, use your own fork URL (e.g. `https://github.com/mrizzi/xavier-analytics.git`) instead of using `https://github.com/project-xavier/xavier-analytics.git` in the [Decision Manager](https://github.com/project-xavier/xavier-integration#decision-manager) section
1. go to `Settings` page in Business Central project
1. copy the `ssh` URL (e.g. `ssh://analytics-rhdmcentr-2-9ws6w:8001/MySpace/xavier-analytics`)
1. from your local terminal, login to the local OKD instance as `developer` and switch to use the migration analytics project
1. `oc port-forward ` `oc get pod | grep "^analytics-rhdmcentr" | awk '{print $1}'`` 8001:8001`
1. from your `xavier-analytics` project root folder, execute `git remote add remote-dm ssh://adminUser@localhost:8001/MySpace/xavier-analytics` replacing the remote host's name (e.g. `analytics-rhdmcentr-2-9ws6w`) with `localhost`
1. retrieve the code from Decision Manager Business Central using `git pull remote-dm MIGENG-45`

## Known issues
### Business Central project deployment
When deploying the latest master in Business Central, it could fail due to a missing dependency.
To solve the issue:
1. go to the `Settings` tab in Business Central main project page
1. select `Dependencies` from left menu
1. add the dependency:
   1. Group ID `org.mockito`
   1. Artifact ID `mockito-core`
   1. Version `2.28.2`
1. save it (if it complains for the project name, change it to `Xavier-Analytics` in `General Settings`)

Now it will be safe the `Build & Install` and `Deploy` the project.
