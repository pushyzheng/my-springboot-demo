package site.pushy.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换JSON工具类
 * @author Pushy
 * @created 2018-7-7 10:06:32
 */

public class RespEntity {

    public RespEntity() {}

    public static String toJsonString(Integer code, String message, Object data) {
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("message",message);
        jsonMap.put("code",code);
        jsonMap.put("data", data);
        //return JSONObject.toJSONString(jsonMap, SerializerFeature.WriteMapNullValue);
        return JSONObject.toJSONString(jsonMap);
    }

    public static String message(Object message) {
        return JSONObject.toJSONString(message, SerializerFeature.WriteMapNullValue);
    }

    public static String success(Object data) {
        return toJsonString(200, "", data);
    }

    public static String error(Integer code, String message) {
        return toJsonString(code, message, null);
    }

}
