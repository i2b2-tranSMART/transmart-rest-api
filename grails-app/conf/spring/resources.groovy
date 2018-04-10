import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

beans = {
	handleAllExceptionsBeanFactoryPostProcessor(HandleAllExceptionsBeanFactoryPostProcessor)
}

@CompileStatic
class HandleAllExceptionsBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.getBeanDefinition('businessExceptionResolver').propertyValues.addPropertyValue(
				'handleAll', true)
	}
}
