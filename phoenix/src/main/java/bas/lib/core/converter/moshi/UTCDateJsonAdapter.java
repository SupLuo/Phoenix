package bas.lib.core.converter.moshi;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.text.ParseException;
import java.util.Date;

import bas.lib.core.date.DateUtils;
import bas.lib.core.lang.StringUtils;

/**
 * 对Date类型进行解析
 */
public class UTCDateJsonAdapter {

    private boolean isSupportTimestamp = false;

    UTCDateJsonAdapter() {
        this(false);
    }

    /**
     * @param isSupportTimestamp 是否支持时间戳格式
     */
    UTCDateJsonAdapter(boolean isSupportTimestamp) {
        this.isSupportTimestamp = isSupportTimestamp;
    }

    @ToJson
    String toJson(Date date) {
        if (date == null)
            return null;
        return DateUtils.toUTCDateTimeFormat(date);
    }

    @FromJson
    Date fromJson(String json) throws ParseException {
        if (StringUtils.isNullOrEmpty(json))
            return null;
        //纯数字日期，视为时间戳处理
        if (isSupportTimestamp && StringUtils.isDigitsOnly(json)) {
            long timestamp = Long.parseLong(json);
            return new Date(timestamp);
        }
        return DateUtils.getUTCDateTimeFormat().parse(json);
    }
}