/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// WARNING: Although it may appear that the body of this method can be easily
		// refactored to avoid the use of multiple loops and multiple lists, the use
		// of multiple lists and multiple passes over the names of processors is
		// intentional. We must ensure that we honor the contracts for PriorityOrdered
		// and Ordered processors. Specifically, we must NOT cause processors to be
		// instantiated (via getBean() invocations) or registered in the ApplicationContext
		// in the wrong order.
		//
		// Before submitting a pull request (PR) to change this method, please review the
		// list of all declined PRs involving changes to PostProcessorRegistrationDelegate
		// to ensure that your proposal does not result in a breaking change:
		// https://github.com/spring-projects/spring-framework/issues?q=PostProcessorRegistrationDelegate+is%3Aclosed+label%3A%22status%3A+declined%22
		// 【1】首先调用BeanDefinitionRegistryPostProcessors
		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		Set<String> processedBeans = new HashSet<>();
		// 传入的beanFactory(DefaultListableBeanFactory实例)实现了BeanDefinitionRegistry接口哈
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
			// 对传入的beanFactoryPostProcessors进行分类，分别添加至regularPostProcessors或registryProcessors集合
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					registryProcessor.postProcessBeanDefinitionRegistry(registry); // 对于已经添加至AbstractApplicationContext的beanFactoryPostProcessors集合的BeanDefinitionRegistryPostProcessor类型的后置处理器需要优先调用哈，比如springboot的CachingMetadataReaderFactoryPostProcessor,ConfigurationWarningsPostProcessor属于这种类型
					registryProcessors.add(registryProcessor); // BeanDefinitionRegistryPostProcessor类型后置处理器添加至registryProcessors集合
				}
				else {
					regularPostProcessors.add(postProcessor); // 普通BeanFactoryPostProcessor后置处理器添加至regularPostProcessors集合
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
			// 此时从beanFactory中取出BeanDefinitionRegistryPostProcessor类型的BeanNames，此时符合条件的只有ConfigurationClassPostProcessor，这个后置处理器在新建AnnotatedBeanDefinitionReader时作为bd加入到了beanFactory，注意springboot环节下的比如springboot的CachingMetadataReaderFactoryPostProcessor,ConfigurationWarningsPostProcessor是直接new新建的，没有作为bd加入beanFactory的
			// 【优先处理实现PriorityOrdered接口的BeanDefinitionRegistryPostProcessor】First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// 【第一大步】先处理容器中的BeanDefinitionRegistryPostProcessor类型的bean，不会加载出只实现了BeanFactoryPostProcessor而没实现BeanDefinitionRegistryPostProcessor的bean哈（TODO 待验证）
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {// ConfigurationClassPostProcessor实现了PriorityOrdered接口
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));// 此时会实例化ConfigurationClassPostProcessor并放入beanFactory,最后加入currentRegistryProcessors集合
					processedBeans.add(ppName); // 并将处理过的BeanDefinitionRegistryPostProcessor的name加入processedBeans以标识已处理，防止后面重复处理哈
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory); // TODO 待分析：根据Order排序
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());// 重要：这里会调用ConfigurationClassPostProcessor后置处理器加载加载所有的bd到registry，同时这里也会会调用到ImportBeanDefinitionRegistrar子类的registerBeanDefinitions方法来注册bd，也就是ImportBeanDefinitionRegistrar比BeanDefinitionRegistryPostProcessor先执行的原因
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup()); // TODO 待分析：如果这里注册的BeanDefinitionRegistryPostProcessor实现类也实现了PriorityOrdered或Ordered接口，此时岂不是不能优先处理了？
				currentRegistryProcessors.clear();
			}

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory); // 因为BeanDefinitionRegistryPostProcessor类型实现类的postProcessBeanDefinitionRegistry方法已经调用过，这里调用前面所有BeanDefinitionRegistryPostProcessor类型实现类的postProcessBeanFactory方法哈
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory); // 调用普通的BeanFactoryPostProcessor类型实现类的postProcessBeanFactory方法
		}

		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}
		/*********************************************************************************************************************************************
		                                                                 所有bd已经加载完成！
		到了这里，一般认为所有类型的bd（包括@Component,ImportBeanDefinitionRegistrar,@Bean，BeanDefinitionRegistryPostProcessor等等类型的bd）已经加载进了容器即
		 加载到了DefaultListableBeanFactory的beanDefinitionMap和beanDefinitionNames集合，后面的BeanFactoryPostProcessor一般不用来注册（添加）bd了，一般注册（添加）bd
		 都是用BeanDefinitionRegistryPostProcessor哈，而BeanFactoryPostProcessor可以修改容器中的bd信息，这在修改容器的bd信息大有用武之地，可参考PropertySourcesPlaceholderConfigurer（TODO 属性相关待分析）
		 *********************************************************************************************************************************************/
		// 【第二大步】然后处理容器中的BeanFactoryPostProcessor类型的bean，虽然也会从beanFactory中加载出BeanDefinitionRegistryPostProcessor类型的bean,但有processedBeans做了标记，根据标记跳过即可
		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest. // TODO 待分析：这里调用BeanFactoryPostProcessor类型的后置处理器，不存在在postProcessBeanFactory方法中再注册BeanFactoryPostProcessor类型的bd吗？如果存在的话，岂不是调用不了新注册的BeanFactoryPostProcessor类型的后置处理方法了么？
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) { // 因为beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class,xxx,xxx)是从beanFactory取出的后置处理器，因此processedBeans集合只会标记从beanFactory中取出的后置处理器哈，对于用户传入的beanFactoryPostProcessors不会标记。
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		// WARNING: Although it may appear that the body of this method can be easily
		// refactored to avoid the use of multiple loops and multiple lists, the use
		// of multiple lists and multiple passes over the names of processors is
		// intentional. We must ensure that we honor the contracts for PriorityOrdered
		// and Ordered processors. Specifically, we must NOT cause processors to be
		// instantiated (via getBean() invocations) or registered in the ApplicationContext
		// in the wrong order.
		//
		// Before submitting a pull request (PR) to change this method, please review the
		// list of all declined PRs involving changes to PostProcessorRegistrationDelegate
		// to ensure that your proposal does not result in a breaking change:
		// https://github.com/spring-projects/spring-framework/issues?q=PostProcessorRegistrationDelegate+is%3Aclosed+label%3A%22status%3A+declined%22
		// 这里会从spring容器中获取到AutowiredAnnotationBeanPostProcessor和CommonAnnotationBeanPostProcessor两种类型的beanPostProcessor的bdNames，这两个哥们均在AnnotationConfigUtils.registerAnnotationConfigProcessors方法中注册即注册到了DefaultListableBeanFactory的beanDefinitionMap集合
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
		// QUSTION:弄明白BeanPostProcessorChecker的作用 ANSWER:因为bpp总是先于普通bean创建，因此BeanPostProcessorChecker的作用就是发现普通bean若比某些bpp先创建，此时会打印日志警告，因为这个bean将不会被这些bpp处理了。比如某个配置类里面的@Bean方法创建了一个bpp（这个方法没有static修饰哈，static修饰则不会）
		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length; // 此时new AnnotationConfigApplicationContext(AnnoConfig.class);的情况下，此时已经向AbstractBeanFactory的beanPostProcessors集合加入了3个beanPostProcessor，分别为调用prepareBeanFactory加入的ApplicationContextAwareProcessor和ApplicationListenerDetector，以及调用ConfigurationClassPostProcessor.postProcessBeanFactory方法加入的ImportAwareBeanPostProcessor
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));// 新建一个BeanPostProcessorChecker,因此最终的要创建的beanPostProcessor数量(DefaultListableBeanFactory的bd数量和AbstractBeanFactory的beanPostProcessors集合实例数量 )+BeanPostProcessorChecker(1)
		// 跟BeanFactoryPostProcessor一样，根据PriorityOrdered,Ordered, and the rest分离beanPostProcessor
		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>(); // TODO MergedBeanDefinitionPostProcessor的作用？
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) { // 遍历从容器中取出来的beanPostProcessor的bd name
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {// beanPostProcessor实现了PriorityOrdered接口，加入priorityOrderedPostProcessors集合
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp); // 如果beanPostProcessor还实现了MergedBeanDefinitionPostProcessor接口，那么将beanPostProcessor加入internalPostProcessors集合
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {// beanPostProcessor实现了Ordered接口，ppName先加入orderedPostProcessorNames集合，下面再处理
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName); // beanPostProcessor没实现任何Ordered接口，ppName先加入nonOrderedPostProcessorNames集合，下面再处理
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory); // 排序
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors); // 将装有bpp实例的priorityOrderedPostProcessors集合批量添加至AbstractBeanFactory的beanPostProcessors集合中

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size()); // 处理前面添加的orderedPostProcessorNames集合并将里面的bpp实例化进行注册
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size()); // 处理前面添加的nonOrderedPostProcessorNames集合并将里面的bpp实例化进行注册
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);// nonOrderedPostProcessors集合不用排序，么有实现顺序接口

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory); // 对前面凡是实现了MergedBeanDefinitionPostProcessor接口的bpp进行排序
		registerBeanPostProcessors(beanFactory, internalPostProcessors);// 并将internalPostProcessors集合的MergedBeanDefinitionPostProcessor类型的bpp（虽然有些实现了ordered等接口）放到AbstractBeanFactory的beanPostProcessors集合后面（位于非Ordered接口的bpp后面），注意这里不会重复注册哈，虽然AbstractBeanFactory的beanPostProcessors集合是CopyOnWriteArrayList，但是AbstractBeanFactory.addBeanPostProcessors会移除原来的再放到后面

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc). // TODO QUESTION:为何MergedBeanDefinitionPostProcessor类型的bpp和ApplicationListenerDetector这个bpp总是要放到最后面？
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext)); // AbstractBeanFactory的beanPostProcessors集合前面已经添加了一个ApplicationListenerDetector实例，现在重新新建一个ApplicationListenerDetector实例覆盖原来的并放到集合最后面，注意：因为ApplicationListenerDetector重写了equals方法，只要是同一个ApplicationListenerDetector和同一个容器内的都认为是equals，所以不会重复添加
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {
		// 遍历BeanDefinitionRegistryPostProcessor集合postProcessors，一个个分别调用其postProcessBeanDefinitionRegistry方法
		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process")
					.tag("postProcessor", postProcessor::toString);
			postProcessor.postProcessBeanDefinitionRegistry(registry);
			postProcessBeanDefRegistry.end();
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			StartupStep postProcessBeanFactory = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process")
					.tag("postProcessor", postProcessor::toString);
			postProcessor.postProcessBeanFactory(beanFactory);
			postProcessBeanFactory.end();
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		if (beanFactory instanceof AbstractBeanFactory) {
			// Bulk addition is more efficient against our CopyOnWriteArrayList there
			((AbstractBeanFactory) beanFactory).addBeanPostProcessors(postProcessors);
		}
		else {
			for (BeanPostProcessor postProcessor : postProcessors) {
				beanFactory.addBeanPostProcessor(postProcessor);
			}
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}
		// this.beanPostProcessorTargetCoun：最终要创建的bpp实例数量，如果在创建bpp期间，有一个bean创建了且该bean不是bpp和spring内部的基础bean的话，此时就会检查目前的bpp实例数量是不是小于要创建的目标bpp数量beanPostProcessorTargetCount，若小于，说明有一个bean比一些bpp还要提前创建，此时这个bean的生命周期就不会被这些bpp处理了（bpp的创建总会比普通bean线创建哈）
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
