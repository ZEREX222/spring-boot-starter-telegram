package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver.ScenarioIdData;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.utils.Assert;

/**
 * The {@code ScenarioUpdateHandlerPopular} class implements the {@link PriorityPreUpdateFilter} interface and is
 * responsible for handling the pre-update filtering of scenarios.
 *
 * @param <S> the type of the scenario
 */
public class ScenarioUpdateHandlerPopular<S> implements PriorityPreUpdateFilter {

    private final ScenarioPersister<S> persister;
    private final ScenarioFactory<S> scenarioFactory;
    private final ScenarioIdResolver scenarioIdResolver;

    /**
     * Constructs a {@code ScenarioUpdateHandlerPopular} with the specified parameters.
     *
     * @param persister          the {@link ScenarioPersister} used to persist scenarios
     * @param scenarioFactory    the {@link ScenarioFactory} used to create scenarios
     * @param scenarioIdResolver the {@link ScenarioIdResolver} used to resolve scenario IDs
     */
    public ScenarioUpdateHandlerPopular(
        ScenarioPersister<S> persister,
        ScenarioFactory<S> scenarioFactory, ScenarioIdResolver scenarioIdResolver
    ) {
        Assert.required(persister, "ScenarioPersister");
        Assert.required(scenarioFactory, "ScenarioFactory");
        Assert.required(scenarioIdResolver, "ScenarioIdResolver");

        this.persister = persister;
        this.scenarioFactory = scenarioFactory;
        this.scenarioIdResolver = scenarioIdResolver;
    }

    /**
     * This method is called before an update request is processed. It resolves the scenario ID from the request,
     * creates a scenario using the {@link ScenarioFactory}, and restores the scenario state using the
     * {@link ScenarioPersister}. If the scenario matches the update request, it sets the scenario in the request.
     *
     * @param request the {@link UpdateRequest} that contains the details of the update
     */
    @Override
    public void preFilter(UpdateRequest request) {
        Scenario<S> scenario = null;
        ScenarioIdData idData = scenarioIdResolver.resolveId(request);
        for (String id : idData.ids()) {
            if (scenario == null) {
                Scenario<S> possibleScenario = persister.restore(scenarioFactory.create(id), id);
                if (possibleScenario.matches(request)) {
                    scenario = possibleScenario;
                }
            }
        }
        if (scenario == null && idData.fallbackId() != null) {
            String fallbackId = idData.fallbackId();
            Scenario<S> fallbackScenario = persister.restore(scenarioFactory.create(fallbackId), fallbackId);
            if (fallbackScenario.matches(request)) {
                scenario = fallbackScenario;
            }
        }
        if (scenario == null) {
            Scenario<S> clearScenario = scenarioFactory.create(scenarioIdResolver.generateId(request));
            if (clearScenario.matches(request)) {
                scenario = clearScenario;
            }
        }
        if (scenario != null) {
            request.getAccessor().setScenario(scenario);
        }
    }

    @Override
    public int getPreOrder() {
        return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
    }
}
