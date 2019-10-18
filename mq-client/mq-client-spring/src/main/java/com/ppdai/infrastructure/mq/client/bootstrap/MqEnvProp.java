package com.ppdai.infrastructure.mq.client.bootstrap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MqEnvProp implements EnvironmentAware {
	private Environment environment;
	private final Sanitizer sanitizer = new Sanitizer();
	public Map<String, Map<String, Object>> getAllEnv() {  
		Map<String, Map<String, Object>> result = new LinkedHashMap<String, Map<String, Object>>();		
		for (Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
			PropertySource<?> source = entry.getValue();
			String sourceName = entry.getKey();
			if (source instanceof EnumerablePropertySource) {
				EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
				Map<String, Object> properties = new LinkedHashMap<String, Object>();
				for (String name : enumerable.getPropertyNames()) {
					properties.put(name, sanitize(name, enumerable.getProperty(name)));
				}
				result.put(sourceName, properties);				
			}
		}
		return result;
	}
	
	public Map<String, Object> getEnv() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();		
		for (Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
			PropertySource<?> source = entry.getValue();			
			if (source instanceof EnumerablePropertySource) {
				EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
				for (String name : enumerable.getPropertyNames()) {
					if(!result.containsKey(name)){
						result.put(name, sanitize(name, enumerable.getProperty(name)));
					}
				}				
			}
		}
		return result;
	}
	public Map<String, Object> getEnv(String prefix) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();		
		for (Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
			PropertySource<?> source = entry.getValue();			
			if (source instanceof EnumerablePropertySource) {
				EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
				for (String name : enumerable.getPropertyNames()) {
					if(!result.containsKey(name)&&name.startsWith(prefix)&&!prefix.equals(name)){
						result.put(name, sanitize(name, enumerable.getProperty(name)));
					}
				}				
			}
		}
		return result;
	}

	private Map<String, PropertySource<?>> getPropertySources() {
		Map<String, PropertySource<?>> map = new LinkedHashMap<String, PropertySource<?>>();
		MutablePropertySources sources = null;		
		if (environment != null && environment instanceof ConfigurableEnvironment) {
			sources = ((ConfigurableEnvironment) environment).getPropertySources();
		}
		else {
			sources = new StandardEnvironment().getPropertySources();
		}
		for (PropertySource<?> source : sources) {
			extract("", map, source);
		}
		return map;
	}

	private void extract(String root, Map<String, PropertySource<?>> map,
			PropertySource<?> source) {
		if (source instanceof CompositePropertySource) {
			for (PropertySource<?> nest : ((CompositePropertySource) source)
					.getPropertySources()) {
				extract(source.getName() + ":", map, nest);
			}
		}
		else {
			map.put(root + source.getName(), source);
		}
	}

	public Object sanitize(String name, Object object) {
		return this.sanitizer.sanitize(name, object);
	}

	/**
	 * Apply any post processing to source data before it is added.
	 * @param sourceName the source name
	 * @param properties the properties
	 * @return the post-processed properties or {@code null} if the source should not be
	 * added
	 * @since 1.4.0
	 */
	protected Map<String, Object> postProcessSourceProperties(String sourceName,
			Map<String, Object> properties) {
		return properties;
	}
	
	class Sanitizer {

		private final String[] REGEX_PARTS = { "*", "$", "^", "+" };

		private Pattern[] keysToSanitize;

		Sanitizer() {
			this("password", "secret", "key", "token", ".*credentials.*", "vcap_services");
		}

		Sanitizer(String... keysToSanitize) {
			setKeysToSanitize(keysToSanitize);
		}

		/**
		 * Keys that should be sanitized. Keys can be simple strings that the property ends
		 * with or regex expressions.
		 * @param keysToSanitize the keys to sanitize
		 */
		public void setKeysToSanitize(String... keysToSanitize) {
			Assert.notNull(keysToSanitize, "KeysToSanitize must not be null");
			this.keysToSanitize = new Pattern[keysToSanitize.length];
			for (int i = 0; i < keysToSanitize.length; i++) {
				this.keysToSanitize[i] = getPattern(keysToSanitize[i]);
			}
		}

		private Pattern getPattern(String value) {
			if (isRegex(value)) {
				return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			}
			return Pattern.compile(".*" + value + "$", Pattern.CASE_INSENSITIVE);
		}

		private boolean isRegex(String value) {
			for (String part : REGEX_PARTS) {
				if (value.contains(part)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Sanitize the given value if necessary.
		 * @param key the key to sanitize
		 * @param value the value
		 * @return the potentially sanitized value
		 */
		public Object sanitize(String key, Object value) {
			for (Pattern pattern : this.keysToSanitize) {
				if (pattern.matcher(key).matches()) {
					return (value == null ? null : "******");
				}
			}
			return value;
		}

	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment=environment;
	}

}
