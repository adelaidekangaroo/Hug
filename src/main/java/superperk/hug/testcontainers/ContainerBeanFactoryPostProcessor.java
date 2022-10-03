package superperk.hug.testcontainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import superperk.hug.testcontainers.annotations.ContainerDependencies;
import superperk.hug.testcontainers.definitions.AbstractContainerDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * Определение порядка создая бинов и регистрация ContainerBeanPostProcessor
 */
public final class ContainerBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private final List<AbstractContainerDefinition> abstractContainerDefinitions;

    public ContainerBeanFactoryPostProcessor(@NotNull List<AbstractContainerDefinition> abstractContainerDefinitions) {
        this.abstractContainerDefinitions = abstractContainerDefinitions;
    }

    private static boolean filterContainerTypesWithDependencies(AbstractContainerDefinition abstractContainerDefinition) {
        return abstractContainerDefinition.getContainerType()
                .getBeanType()
                .isAnnotationPresent(ContainerDependencies.class);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        abstractContainerDefinitions.stream()
                .filter(ContainerBeanFactoryPostProcessor::filterContainerTypesWithDependencies)
                .map(ContainerBeanDefinition::of)
                .forEach(containerBeanDefinition ->
                        Arrays.stream(containerBeanDefinition.getContainerDependencies().initBefore())
                                .forEach(dependentBeanType -> {
                                    var beanNamesByDependentBeanType = beanFactory.getBeanNamesForType(dependentBeanType);
                                    Arrays.stream(beanNamesByDependentBeanType)
                                            .map(beanFactory::getBeanDefinition)
                                            .forEach(beanDefinitionByDependentBeanType ->
                                                    beanDefinitionByDependentBeanType.setDependsOn(containerBeanDefinition.getBeanName()));
                                }));

        beanFactory.addBeanPostProcessor(new ContainerBeanPostProcessor(abstractContainerDefinitions));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Getter
    @AllArgsConstructor
    private static class ContainerBeanDefinition {
        private String beanName;
        private ContainerDependencies containerDependencies;

        public static ContainerBeanDefinition of(AbstractContainerDefinition abstractContainerDefinition) {
            return new ContainerBeanDefinition(
                    abstractContainerDefinition.getContainerType().getBeanName(),
                    abstractContainerDefinition.getContainerType().getBeanType().getAnnotation(ContainerDependencies.class)
            );
        }
    }
}