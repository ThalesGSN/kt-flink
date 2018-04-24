package br.com.dti.kt.core.common;

import br.com.dti.kt.core.Constants;
import br.com.dti.kt.domain.ProcessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class MapperUtils {

    private MapperUtils() {

    }

    public static <T> T convert(Object obj, Class<T> classOfT, T value) {
        try {
            return obj != null ? (T) ConvertUtils.convert(obj, classOfT) : value;
        } catch (ClassCastException | ConversionException e) {
            log.error(String.format("Can't convert %s to %s", obj.getClass(), classOfT), e);
            return value;
        }
    }

    public static <T> T convert(Object obj, Class<T> classOfT) {
        return convert(obj, classOfT, null);
    }
    
    public static java.util.Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    	
    }

    public static Date getDate(ParameterTool parameter) {
        String startDate = parameter.get(Constants.JOB_DATE_START_PARAMETER_NAME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.toDate(startDate));
        String processType = parameter.get(Constants.JOB_PROCESS_TYPE_PARAMETER_NAME);
        if (StringUtils.isBlank(processType) || processType.equals(ProcessType.DAILY.name())) {
            return new Date(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1, // XXX: Month value is 0-based. e.g., 0 for January.
                calendar.get(Calendar.YEAR)
            );
        }

        return new Date(null, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    @Getter
    @AllArgsConstructor
    public static class Date {
        private @Getter @Setter Integer day;
        private @Getter @Setter Integer month;
        private @Getter @Setter Integer year;
    }

}
