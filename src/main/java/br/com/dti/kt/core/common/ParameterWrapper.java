package br.com.dti.kt.core.common;

import br.com.dti.kt.core.Constants;
import br.com.dti.kt.domain.ProcessType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class ParameterWrapper {

    private static final String CONFIG_FILE_DEFAULT = "application";

    private ParameterWrapper() {

    }

    public static ParameterTool fromArgs(String[] args) {
       // log.info("Initialize ParameterTool with {}" , Lists.newArrayList(args));

        ParameterTool parameter = ParameterTool.fromSystemProperties();
        parameter = parameter.mergeWith(getParameterToolFromConfigFileDefault());
        parameter = parameter.mergeWith(getParameterToolFromConfigFile(args));
        parameter = parameter.mergeWith(ParameterTool.fromArgs(args));
        
        
        return parameter;
    }
    
    public static ParameterTool fromArgs(String[] args, boolean dateIsNecessary, boolean lastMonths) {
        log.info("Initialize ParameterTool with {}", Lists.newArrayList(args));

        ParameterTool parameter = ParameterTool.fromSystemProperties();
        parameter = parameter.mergeWith(getParameterToolFromConfigFileDefault());
        parameter = parameter.mergeWith(getParameterToolFromConfigFile(args));
        parameter = parameter.mergeWith(ParameterTool.fromArgs(args));
        
        if(dateIsNecessary){
        	validateWithDate(parameter);
        } else {
        	validateCleanDataBase(parameter);
        	if(lastMonths) {
        		validateLastMonths(parameter);
        	}
        }
        
        return parameter;
    }

    private static void validateWithDate(ParameterTool parameter) {
        String startDate = parameter.get(Constants.JOB_DATE_START_PARAMETER_NAME);
        if (StringUtils.isBlank(startDate)) {
            throw new IllegalArgumentException("Required parameter 'job.date.start' was not passed");
        }

        DateUtils.toDate(startDate);

        String endDate = parameter.get(Constants.JOB_DATE_END_PARAMETER_NAME);
        if (StringUtils.isBlank(endDate)) {
            throw new IllegalArgumentException("Required parameter 'job.date.end' was not passed");
        }

        DateUtils.toDate(endDate);

        String processType = parameter.get(Constants.JOB_PROCESS_TYPE_PARAMETER_NAME);
        if (StringUtils.isNotBlank(processType)) {
            try {
//                Lists.newArrayList(ProcessType.values()).stream()
//                    .filter(p -> p.name().equals(processType))
//                    .findFirst()
//                    .orElseThrow(NoSuchElementException::new);
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException(
                    String.format("Invalid parameter 'job.process.type', the types are: %s", Arrays.asList(ProcessType.values())),
                    e);
            }
        }
        
        validateCleanDataBase(parameter);
    }
    
    private static void validateCleanDataBase(ParameterTool parameter) {
    	
        String cleanDataBase = parameter.get(Constants.CLEAN_DATABASE);
        
        if (StringUtils.isBlank(cleanDataBase)) {
        	throw new IllegalArgumentException("Required parameter 'cleanDataBase' was not passed");
        }
        
        if (!"0".equals(cleanDataBase) && !"1".equals(cleanDataBase)) {
        	throw new IllegalArgumentException("Parameter 'cleanDataBase' can only be 0 or either 1");
        }
        
    }
    
    private static void validateLastMonths(ParameterTool parameter) {
    	
        String lastMonths = parameter.get(Constants.LAST_MONTHS);
        
        if (StringUtils.isBlank(lastMonths)) {
        	throw new IllegalArgumentException("Required parameter 'lastMonths' was not passed");
        }
        
        if (!lastMonths.matches("[0-9]+")) {
        	throw new IllegalArgumentException("Parameter 'lastMonths' can only be positive integer");
        }
        
    }

    private static ParameterTool getParameterToolFromConfigFileDefault() {
        String configFile = String.format("%s.properties", CONFIG_FILE_DEFAULT);
        return getParameterToolFromFile(configFile);
    }

    private static ParameterTool getParameterToolFromConfigFile(String[] args) {
        ParameterTool parameter = ParameterTool.fromArgs(args);
        if (StringUtils.isBlank(parameter.get(Constants.JOB_PROFILES_ACTIVE))) {
            return ParameterTool.fromMap(new HashMap<String, String>());
        }
        String configFile = String.format("%s-%s.properties", CONFIG_FILE_DEFAULT, parameter.get(Constants.JOB_PROFILES_ACTIVE));
        return getParameterToolFromFile(configFile);
    }

    private static ParameterTool getParameterToolFromFile(String configFile) {
        InputStream input = null;
        Properties properties = new Properties();
        try {
            input = ParameterWrapper.class.getClassLoader().getResourceAsStream(configFile);
            properties.load(input);
            properties.forEach((key, value) -> {
                String strVal = String.valueOf(value);
                if (strVal.indexOf('$') == 0) {
                    String newValue = (String) properties.get(strVal.replaceFirst("\\$", ""));
                    properties.setProperty((String) key, newValue);
                }
            });
            return ParameterTool.fromMap((Map) properties);
        } catch (IOException e) {
            throw new IllegalArgumentException("load() failed." + e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.info("Couldn't be closed - " + e.getMessage(), e);
                }
            }
        }
    }
}
