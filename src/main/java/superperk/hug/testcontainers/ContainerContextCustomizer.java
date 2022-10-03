package superperk.hug.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import superperk.hug.testcontainers.definitions.AbstractContainerDefinition;

import java.util.List;

/**
 * Получение доступа к спринг контексту, к активным профилям через окружение, и регистрация BeanFactoryPostProcessor
 */
public final class ContainerContextCustomizer implements ContextCustomizer {

    private final List<AbstractContainerDefinition> abstractContainerDefinitions;

    public ContainerContextCustomizer(@NotNull List<AbstractContainerDefinition> abstractContainerDefinitions) {
        this.abstractContainerDefinitions = abstractContainerDefinitions;
    }

    @Override
    public void customizeContext(@NotNull ConfigurableApplicationContext context,
                                 @NotNull MergedContextConfiguration mergedConfig) {
        context.addBeanFactoryPostProcessor(new ContainerBeanFactoryPostProcessor(abstractContainerDefinitions));
    }
}