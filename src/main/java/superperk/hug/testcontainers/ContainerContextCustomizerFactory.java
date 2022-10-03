package superperk.hug.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizerFactory;
import superperk.hug.testcontainers.annotations.EnableMongoContainer;
import superperk.hug.testcontainers.annotations.EnablePostgresContainer;
import superperk.hug.testcontainers.definitions.AbstractContainerDefinition;
import superperk.hug.testcontainers.definitions.MongoContainerDefinition;
import superperk.hug.testcontainers.definitions.PostgresContainerDefinition;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * Сканирует тест класс на предмет наличия объявленных контейнеров и собирает настройки по каждому их них.
 * Создает ContainerContextCustomizer.
 */
public final class ContainerContextCustomizerFactory implements ContextCustomizerFactory {

    private static boolean filterContainerAnnotations(Annotation annotation) {
        return annotation instanceof EnablePostgresContainer ||
                annotation instanceof EnableMongoContainer;
    }

    private static AbstractContainerDefinition convertContainerAnnotationToContainerDefinition(Annotation annotation) {
        if (annotation instanceof EnablePostgresContainer enablePostgresContainer) {
            return new PostgresContainerDefinition(enablePostgresContainer.image());
        } else if (annotation instanceof EnableMongoContainer enableMongoContainer) {
            return new MongoContainerDefinition(enableMongoContainer.image());
        } else {
            throw new IllegalArgumentException("Invalid container annotation");
        }
    }

    @Override
    public ContainerContextCustomizer createContextCustomizer(@NotNull Class<?> testClass,
                                                              @NotNull List<ContextConfigurationAttributes> configAttributes) {
        var abstractContainerDefinitions = Arrays.stream(testClass.getDeclaredAnnotations())
                .filter(ContainerContextCustomizerFactory::filterContainerAnnotations)
                .map(ContainerContextCustomizerFactory::convertContainerAnnotationToContainerDefinition)
                .toList();
        return new ContainerContextCustomizer(abstractContainerDefinitions);
    }
}