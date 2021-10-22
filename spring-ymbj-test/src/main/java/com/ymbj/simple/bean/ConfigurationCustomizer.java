package com.ymbj.simple.bean;

@FunctionalInterface
public interface ConfigurationCustomizer {

	/**
	 * Customize the given a {@link Configuration} object.
	 * @param configuration the configuration object to customize
	 */
	void customize(Configuration configuration);
}
