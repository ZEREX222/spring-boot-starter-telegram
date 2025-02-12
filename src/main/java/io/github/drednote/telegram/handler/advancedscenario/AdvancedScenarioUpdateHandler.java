package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.*;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioManager;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioState;
import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {

    private final IAdvancedScenarioStorage storage;
    private final List<IAdvancedScenarioConfig> scenariosConfig;


    public AdvancedScenarioUpdateHandler(IAdvancedScenarioStorage storage, List<IAdvancedScenarioConfig> scenariosConfig) {
        super();
        this.storage = storage;
        this.scenariosConfig = scenariosConfig;
    }

    @Override
    public void onUpdate(UpdateRequest request) {
        AdvancedScenarioManager advancedScenarioManager = initAdvancedScenarioManager(scenariosConfig);
        if (advancedScenarioManager != null && !advancedScenarioManager.getScenarios().isEmpty()) {
            Optional<IAdvancedScenarioEntity> advancedScenarioEntity = this.storage.findById(request.getUserId() + ":" + request.getChatId());
            UserScenarioContext<?> context = new UserScenarioContext<>(request, advancedScenarioEntity.map(IAdvancedScenarioEntity::getData).orElse(null));

            @NotNull List<AdvancedScenario<?>> advancedActiveScenarios = advancedScenarioManager.getActiveScenarios();
            for (AdvancedScenario<?> advancedActiveScenario : advancedActiveScenarios) {
                for (UpdateRequestMapping handlerMethod : advancedActiveScenario.getActiveConditions().stream().map(AdvancedScenarioUpdateHandler::fromTelegramRequest).toList()) {
                    if (handlerMethod.matches(request)) {
                        advancedActiveScenario.process(context);
                    }
                }
            }
        }
        ResponseSetter.setResponse(request, null);
    }

    private AdvancedScenarioManager initAdvancedScenarioManager(List<IAdvancedScenarioConfig> scenariosConfig) {
        if (!scenariosConfig.isEmpty()) {
            AdvancedScenarioManager advancedScenarioManager = new AdvancedScenarioManager();
            for (IAdvancedScenarioConfig scenarioConfig : scenariosConfig) {
                advancedScenarioManager.addScenario(scenarioConfig.getName(), scenarioConfig.getScenario());
            }
            return advancedScenarioManager;
        }
        return null;
    }

    private static UpdateRequestMapping fromTelegramRequest(@NonNull TelegramRequest request) {
        String pattern = request.getPatterns().stream().findFirst().orElse(null);
        RequestType requestType = request.getRequestTypes().stream().findFirst().orElse(null);
        MessageType messageType = request.getMessageTypes().stream().findFirst().orElse(null);

        // Create a Set with a single element if messageType is not null, otherwise an empty Set
        Set<MessageType> messageTypes = messageType != null ? Set.of(messageType) : Collections.emptySet();

        return new UpdateRequestMapping(pattern, requestType, messageTypes, request.exclusiveMessageType());
    }


}
