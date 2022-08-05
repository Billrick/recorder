package com.rick.utils;

public class ObjUtils {

    public static Long objDefaultLongVal(Object obj, Long defaultVal) {
        if(obj != null){
            return Long.valueOf((String) obj);
        }
        return defaultVal;
    }
}
