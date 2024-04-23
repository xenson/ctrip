package com.ctrip.car.osd.framework.cache;

import com.ctrip.car.osd.framework.common.utils.HashTwoUtil;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.ctrip.car.osd.framework.common.utils.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DefaultKeyGenerator extends IKeyGenerator{
    @Override
    public String buildKey(String key, Class<?>[] parameterTypes, Object[] arguments) {
        if (key.contains("{")) {
            Pattern pattern = Pattern.compile("\\d+\\.?[\\w]*");
            Matcher matcher = pattern.matcher(key);
            while (matcher.find()) {
                String tmp = matcher.group();
                String express[] = matcher.group().split("\\.");
                String i = express[0];
                int index = Integer.parseInt(i) - 1;
                Object value = arguments[index];
                if (parameterTypes[index].isAssignableFrom(List.class)) {
                    List result = (List) arguments[index];
                    value = String.join(",", result);
                }
                if (value == null || value.equals("null"))
                    value = "";
                if (express.length > 1) {
                    String field = express[1];
                    value = ReflectionUtils.getFieldValue(value, field);
                }
                key = key.replace("{" + tmp + "}", value.toString());
            }
        } else if (arguments.length == 1 && !"all".equalsIgnoreCase(key)) {
            Object argument = arguments[0];
            String data = JsonUtil.toJSONStringExcludeSchema(argument);
            key = HashTwoUtil.getHash(data);
        }
        return key;
    }
}
