package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class AdvancedScenarioManager {
    private final Map<String, AdvancedScenario> scenarios = new HashMap<>();
    private String currentScenarioName;

    public AdvancedScenarioManager() {
    }

    public void addScenario(String name, AdvancedScenario scenario) {
        scenarios.put(name, scenario);
    }

    public AdvancedScenarioManager setCurrentScenario(String scenarioName) {
        if (!scenarios.containsKey(scenarioName)) {
            throw new IllegalArgumentException("Scenario not found: " + scenarioName);
        }
        this.currentScenarioName = scenarioName;
        return this;
    }

    public AdvancedScenario getCurrentScenario() {
        return scenarios.get(currentScenarioName);
    }

    public void process(UserScenarioContext context) {
        while (!context.isEnd) {
            AdvancedScenario currentScenario = getCurrentScenario();
            if (currentScenario == null) {
                throw new RuntimeException("Current scenario not found: " + currentScenarioName);
            }

            try {
                currentScenario.process(context);

                if (context.nextScenario != null) {
                    setCurrentScenario(context.nextScenario);
                    context.nextScenario = null;
                }
            } catch (RuntimeException e) {
                throw e;
            }
        }
    }

    public List<TelegramRequest> getActiveHandlers() {
        return Optional.ofNullable(currentScenarioName)
                .map(name -> getCurrentScenario().getActiveConditions())
                .orElseGet(() -> scenarios.values().stream()
                        .flatMap(scenario -> scenario.getActiveConditions().stream())
                        .collect(Collectors.toList()));
    }
}
