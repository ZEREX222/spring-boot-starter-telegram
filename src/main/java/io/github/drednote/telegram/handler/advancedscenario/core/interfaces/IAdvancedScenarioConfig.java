package io.github.drednote.telegram.handler.advancedscenario.core.interfaces;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;

public interface IAdvancedScenarioConfig {
    String getName();
    AdvancedScenario<?> getScenario();
}
