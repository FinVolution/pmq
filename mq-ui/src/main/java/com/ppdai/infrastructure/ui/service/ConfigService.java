package com.ppdai.infrastructure.ui.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConfigDto;

@Service
public class ConfigService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	private SoaConfig soaConfig;
	
	private Map<String, String> keyMap = new LinkedHashMap<>();
	private Map<String, String> defaultValueMap = new HashMap<>();
	private Map<String, String> desMap = new HashMap<>();
	private Map<String,Method> methodMap=new HashMap<>();
	private String fieldsName = "";
	@PostConstruct
	private void init(){
		Field[] fields = SoaConfig.class.getDeclaredFields();
		Method[] methods = SoaConfig.class.getDeclaredMethods();
		for (int i = 0; i < fields.length; i++) {
			fieldsName = fields[i].getName();
			if (fieldsName.startsWith("env_")) {
				fields[i].setAccessible(true);
				try {
					if (fieldsName.endsWith("_key")) {
						keyMap.put(fieldsName, fields[i].get(soaConfig).toString());
					} else if (fieldsName.endsWith("_defaultValue")) {
						defaultValueMap.put(fieldsName, fields[i].get(soaConfig).toString());
					} else if (fieldsName.endsWith("_des")) {
						desMap.put(fieldsName, fields[i].get(soaConfig).toString());
					}

				} catch (IllegalArgumentException e) {
					//e.printStackTrace();
				} catch (IllegalAccessException e) {
					//e.printStackTrace();
				}
			}
		}


		for (int i=0;i<methods.length;i++){
			methodMap.put(methods[i].getName(),methods[i]);
		}


	}

	public BaseUiResponse<List<ConfigDto>> getConfigData() {
	    List<ConfigDto> configList = new ArrayList<>();
		for (String methodName:methodMap.keySet()) {
			ConfigDto configBo = new ConfigDto();
			try {
				Method method = methodMap.get(methodName);
				if ((method.getParameters() != null && method.getParameters().length > 0)){
					continue;
				}
				if(method.getReturnType().toString().equals("void")){
					continue;
				}
				method.setAccessible(true);
				configBo.setKey(keyMap.get("env_"+methodName+"_key"));
				configBo.setDefaultValue(defaultValueMap.get("env_"+methodName+ "_defaultValue"));
				configBo.setDescription(desMap.get("env_"+methodName + "_des"));
				configBo.setCurrentValue(method.invoke(soaConfig).toString());

				if(!keyMap.containsKey("env_"+methodName+"_key")||!defaultValueMap.containsKey("env_"+methodName+ "_defaultValue")||
						!desMap.containsKey("env_"+methodName + "_des")){
					log.error("soaConfig中"+methodName+"配置不标准。");
				}
				configList.add(configBo);

			}  catch (IllegalAccessException e) {
				//e.printStackTrace();
			} catch (InvocationTargetException e) {
				//e.printStackTrace();
			}
		}

		BaseUiResponse<List<ConfigDto>> uiResponse = new BaseUiResponse<List<ConfigDto>>();
		uiResponse.setData(configList);
		uiResponse.setCount(new Long(configList.size()));
		return uiResponse;
	}
}
