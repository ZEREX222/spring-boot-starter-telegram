package io.github.drednote.telegram.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.filter.internal.DefaultTelegramResponseEnricher;
import io.github.drednote.telegram.filter.internal.TelegramResponseEnricher;
import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.post.NotHandledUpdateFilter;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.post.ScenarioIdPersistFilter;
import io.github.drednote.telegram.filter.pre.AccessPermissionFilter;
import io.github.drednote.telegram.filter.pre.HasRoleRequestFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.handler.UpdateHandlerAutoConfiguration;
import io.github.drednote.telegram.response.resolver.TelegramResponseTypesResolver;
import java.util.Collection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

/**
 * Autoconfiguration class for setting up update filters and related properties.
 *
 * @author Ivan Galushko
 */
@AutoConfiguration
@EnableConfigurationProperties({FilterProperties.class, PermissionProperties.class})
@AutoConfigureAfter(UpdateHandlerAutoConfiguration.class)
public class FiltersAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateFilterProvider updateFilterProvider(
        ObjectProvider<PreUpdateFilter> prefilters,
        ObjectProvider<PostUpdateFilter> postFilters,
        ObjectProvider<ConclusivePostUpdateFilter> conclusivePostUpdateFilters
    ) {
        return new DefaultUpdateFilterProvider(prefilters, postFilters, conclusivePostUpdateFilters);
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleFilter roleFilter(
        @Autowired(required = false) @Nullable PermissionRepositoryAdapter permissionRepositoryAdapter,
        PermissionProperties permissionProperties
    ) {
        return new RoleFilter(permissionRepositoryAdapter, permissionProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessPermissionFilter accessPermissionFilter(PermissionProperties permissionProperties) {
        return new AccessPermissionFilter(permissionProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NotHandledUpdateFilter notHandledUpdateFilter(TelegramProperties telegramProperties) {
        return new NotHandledUpdateFilter(telegramProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public HasRoleRequestFilter hasRoleRequestFilter() {
        return new HasRoleRequestFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScenarioIdPersistFilter scenarioIdUpdater() {
        return new ScenarioIdPersistFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramResponseEnricher telegramResponseEnricher(
        ObjectMapper objectMapper, TelegramProperties telegramProperties,
        TelegramMessageSource messageSource, Collection<TelegramResponseTypesResolver> resolvers
    ) {
        return new DefaultTelegramResponseEnricher(objectMapper, telegramProperties, messageSource, resolvers);
    }
}
