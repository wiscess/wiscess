package com.wiscess.query.config.processor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import com.wiscess.query.provider.IQueryProvider;
import com.wiscess.query.provider.SqlMap;

public class YamlResourceLoader extends YamlPropertySourceLoader implements QueryResourceLoader{

	@Override
	public List<IQueryProvider> load(Resource resource) throws IOException {
		List<IQueryProvider> queryProviderList=new ArrayList<>();
		
		MapPropertySource mapPropertySource=(MapPropertySource)super.load(resource.getDescription(), resource, null);
		
		String[] names=mapPropertySource.getPropertyNames();
		boolean hasPrefix=false;
		boolean noPrefix=false;
		for(String name:names){
			hasPrefix=hasPrefix || name.startsWith("sqls");
			noPrefix =noPrefix  || !name.startsWith("sqls");
		}
		if(hasPrefix){
			MutablePropertySources mapPropertySources=new MutablePropertySources();
			mapPropertySources.addLast(mapPropertySource);
			try {
				queryProviderList.add((SqlMap)bindPropertiesToTarget(mapPropertySources,SqlMap.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (BindException e) {
				e.printStackTrace();
			}
		}
		if(noPrefix){
			SqlMap sqlMap=new SqlMap();
			Map<String, String> map=new HashMap<>();
			Map<String, Object> source=mapPropertySource.getSource();
			for(String key:source.keySet()){
				map.put(key, (String)source.get(key));
			}
			sqlMap.setSqls(map);
			queryProviderList.add(sqlMap);
		}
		return queryProviderList;
	}
	private Object bindPropertiesToTarget(PropertySources propertySources, Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, BindException {

        Constructor<?> constructor = clazz.getConstructor();
        Object newInstance = constructor.newInstance();

        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(newInstance);
        factory.setPropertySources(propertySources);
        factory.setConversionService(new DefaultConversionService());
        //factory.setTargetName("");
        try {
            factory.bindPropertiesToTarget();
        } catch (Exception ex) {
            String targetClass = org.springframework.util.ClassUtils.getShortName(clazz);
            throw new BeanCreationException(clazz.getSimpleName(), "Could not bind properties to " + targetClass + " (" + ")", ex);
        }
        return newInstance;
    }

}
