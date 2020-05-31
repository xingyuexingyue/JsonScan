package com.sangyu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * User: pengyapan
 * Date: 2020/5/25
 * Time: 下午8:43
 */
public class JsonScan {

    public static ArrayList<String> getJsonKeys(String head, JSONObject jsonObject) {
        ArrayList<String> keys = new ArrayList<String>();
        // 1. 获得 JSONObject 的 key 和 value
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
        // 2. 获得 map 的迭代器
        Iterator<Map.Entry<String, Object>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            // 3. 将 key 添加到 keys
            keys.add(head == null ? key : head + key);
            String head1 = head == null ? key + "-" : head + key + "-";
            // 4. 判断 value 是否为 JSONObject 类型
            if (value instanceof JSONObject) {
                // 4.1 是 JSONObject 类型则递归
                ArrayList<String> list = getJsonKeys(head1, (JSONObject) value);
                keys.addAll(list);
            } else if (value instanceof JSONArray) {
                // 4.2 是 JSONArray 类型
                JSONArray jsonArray = (JSONArray) value;
                if (jsonArray.size() != 0) {
                    // 判断 JSONArray 里面的值是否为 JSONObject
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Object object = jsonArray.get(i);
                        if (object instanceof JSONObject) {
                            // 是 JSONObject 类型
                            ArrayList<String> list = getJsonKeys(head1, (JSONObject) object);
                            keys.addAll(list);
                        }
                    }
                }
            }

        }
        return keys;
    }

    public static Map<String, String> getJsonValues(String head, JSONObject jsonObject) {
        Map<String, String> map = new HashMap<String, String>();
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
        Iterator<Map.Entry<String, Object>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            map.put(head == null ? key : head + key, getValueType(value));
            String head1 = head == null ? key + "-" : head + key + "-";
            if (value instanceof JSONObject) {
                Map<String, String> maps = getJsonValues(head1, (JSONObject) value);
                map.putAll(maps);
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                if (jsonArray.size() != 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Object object = jsonArray.get(i);
                        if (object instanceof JSONObject) {
                            Map<String, String> maps = getJsonValues(head1, (JSONObject) object);
                            map.putAll(maps);
                        }
                    }
                }
            }

        }
        return map;
    }

    public static String result(JSONObject origin, JSONObject actual) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> originArray = getJsonKeys(null, origin);
        ArrayList<String> actualArray = getJsonKeys(null, actual);
        if (originArray.containsAll(actualArray) && actualArray.containsAll(actualArray)) {
            Map<String, String> originMap = getJsonValues(null, origin);
            Map<String, String> actualMap = getJsonValues(null, actual);
            if (originMap.equals(actualMap) && actualMap.equals(originMap)) {
                sb.append("比较结果相同");
            } else {
                sb.append("value 比较类型，返回结果中与规范不同的字段是: ");
                sb.append(getDiffMap(originMap, actualMap));
            }
        } else {
            sb.append(" key 比较，返回结果中与规范不同的字段是: ");
            sb.append(getDiffList(originArray, actualArray));
        }
        return sb.toString();
    }

    public static String getValueType(Object o) {
        String str = null;
        if (o instanceof JSONObject) {
            str = "JSONObject";
        } else if (o instanceof Number) {
            str = "Number";
        } else if (o instanceof String) {
            str = "String";
        } else if (o instanceof JSONArray) {
            str = "JSONArray";
        } else if (o == null) {
            str = "null";
        } else if (o instanceof Boolean) {
            str = "Boolean";
        }
        return str;
    }

    public static String getDiffList(ArrayList<String> originlist, ArrayList<String> actuallist) {
        ArrayList<String> diffList = new ArrayList<String>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String s : originlist) {
            map.put(s, 1);
        }
        for (String s : actuallist) {
            Integer count = map.get(s);
            if (count == null) {
                diffList.add(s);
            }
        }

        return diffList.toString();
    }

    public static String getDiffMap(Map<String, String> originMap, Map<String, String> actualMap) {
        ArrayList<String> diffList = new ArrayList<String>();
        for (Map.Entry<String, String> entry : originMap.entrySet()) {
            if (originMap.get(entry.getKey()).equals(actualMap.get(entry.getKey()))) {
                continue;
            }
            diffList.add(entry.getKey());
        }
        return diffList.toString();
    }

    private static JSONObject getJsonObject(String fileName) {
        JSONObject jsonObject1 = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            jsonObject1 = JSON.parseObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject1;
    }

    public static void main(String[] args) {
        JSONObject jsonObject  =  getJsonObject("src/main/resources/protocol.json");
        JSONObject jsonObject1  =  getJsonObject("src/main/resources/actual.json");
        System.out.println(result(jsonObject, jsonObject1));
    }
}