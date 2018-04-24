package br.com.dti.kt.core;

public class Constants {

    public static final int JDBC_INPUT_FORMAT_FETCH_SIZE_DEFAULT = 1000;
    public static final String JOB_PROFILES_ACTIVE = "job.profiles.active";
    public static final String JOB_DATE_START_PARAMETER_NAME = "job.date.start";
    public static final String JOB_DATE_END_PARAMETER_NAME = "job.date.end";
    public static final String JOB_PROCESS_TYPE_PARAMETER_NAME = "job.process.type";
    public static final String CLEAN_DATABASE = "cleanDataBase";
    public static final String LAST_MONTHS = "lastMonths";
    public static final String QUERIES_BASE_DIR = "queries";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String OUTPUT_PARALLELISM = "default.output.properties.parallelism";
    public static final int DEFAULT_PARALLELISM = 1;
    
    public static final boolean DATE_IS_NOT_REQUIRED = false;
    public static final boolean DATE_IS_REQUIRED = true;
    public static final boolean LAST_MONTHS_IS_REQUIRED = true;
    public static final boolean LAST_MONTHS_IS_NOTREQUIRED = false;
    private Constants() {

    }
}
