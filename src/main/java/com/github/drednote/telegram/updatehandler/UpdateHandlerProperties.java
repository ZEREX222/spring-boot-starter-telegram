package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.update-handler")
@Data
public class UpdateHandlerProperties {

  /**
   * Enabled {@link MvcUpdateHandler}
   */
  private boolean mvcEnabled = true;
  /**
   * Enabled {@link ScenarioUpdateHandler}
   */
  private boolean scenarioEnabled = true;
  /**
   * If in the end of update handling the response is null, set {@link NotHandledHandlerResponse} as
   * response
   */
  private boolean setDefaultAnswer = true;
}
