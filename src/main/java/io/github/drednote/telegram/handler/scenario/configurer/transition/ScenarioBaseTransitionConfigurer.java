package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.ActionContext;
import java.util.Map;

/**
 * Interface for configuring scenario base transitions.
 *
 * @param <C> the type of the configurer
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioBaseTransitionConfigurer<C extends ScenarioBaseTransitionConfigurer<C, S>, S> {

    /**
     * Sets the source state for the transition.
     *
     * @param source the source state for the transition
     * @return the current instance of the configurer
     */
    C source(S source);

    /**
     * Sets the target state for the transition.
     *
     * @param target the target state for the transition
     * @return the current instance of the configurer
     */
    C target(S target);

    /**
     * Sets an action to be executed during the transition.
     *
     * @param action the action to be executed
     * @return the current instance of the configurer
     */
    C action(Action<S> action);

    /**
     * Sets a condition that must be met for a given transition to be called. The matching is executing by
     * {@link UpdateRequestMapping}
     *
     * @param telegramRequest the TelegramRequest to set
     * @return the current instance of the configurer
     * @see UpdateRequestMapping
     */
    C telegramRequest(TelegramRequest telegramRequest);

    /**
     * Sets the additional props to be used during the transition.
     *
     * @param props additional props to pass to {@link Action} in {@link ActionContext}
     * @return the current instance of the configurer
     */
    C props(Map<String, Object> props);

    /**
     * Finalizes the transition configuration and returns a ScenarioTransitionConfigurer.
     * <p>
     * <b>You should always call this method after finishing configuring transition, even if
     * configured transition is last</b>
     *
     * @return a ScenarioTransitionConfigurer to continue the configuration
     */
    ScenarioTransitionConfigurer<S> and();
}
