/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.env;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Strategy to load '.properties' files into a {@link PropertySource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.0.0
 */
public class PropertiesPropertySourceLoader implements PropertySourceLoader {

	private static final String XML_FILE_EXTENSION = ".xml";

	@Override
	public String[] getFileExtensions() {
		return new String[] { "properties", "xml" };
	}

	/**
	 * 加载指定配置文件
	 * @param name the root name of the property source. If multiple documents are loaded
	 * an additional suffix should be added to the name for each source loaded.
	 * @param resource the resource to load
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
		//调用load方法进行加载并返回map形式的数据
		Map<String, ?> properties = loadProperties(resource);
		if (properties.isEmpty()) {
			return Collections.emptyList();
		}
		//对返回结果进行处理和转换
		return Collections
				.singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(properties), true));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	/**
	 * 具体加载过程
	 */
	private Map<String, ?> loadProperties(Resource resource) throws IOException {
		String filename = resource.getFilename();
		//加载xml格式
		if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
			return (Map) PropertiesLoaderUtils.loadProperties(resource);
		}
		//加载properties格式
		return new OriginTrackedPropertiesLoader(resource).load();
	}

}
