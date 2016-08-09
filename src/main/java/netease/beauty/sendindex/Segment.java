package netease.beauty.sendindex;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hzqianwei on 2016/7/20.
 */

public class Segment {

    public Map<String, String> attMap = null;
    public Map<String, Object> publicMap = null;
    public String textAttrs = null;
    public JSONObject json = null;
    public String indexName = null;

    public Segment(Map<String, Object> publicMap, Map<String, String> attMap, String indexName) {
        this.publicMap = publicMap;
        this.attMap = attMap;
        this.indexName = indexName;
    }


    public Segment(String indexName) {
        this.indexName = indexName;
    }

    /**
     *
     * 传入一个map, 这个map对应的是 brand 的 text_atts里面的属性,
     * @return 返回的是String类型的字符串
     */
    public String getBrandTextAttrs(){
        System.out.println("---------------getBrandTextAttrs开始-----------------------------------------------");

        StringBuffer sb = new StringBuffer();

        int i = 0;
        for (String key: attMap.keySet()) {
            String value = attMap.get(key);

            if (value != null) {
                value = value.trim();
            }

            if (!"null".equals(value) && value != null && !"".equals(value)) {
                // 如果有分号, 就要拆成多个
                System.out.println(key + ":" +attMap.get(key));
                if(value.contains(";")){
                    String[] valueList = value.split(";");
                    for (String sonValue:valueList) {
                        String typeString = key + "_" + sonValue + "^" + String.valueOf(i) + "~1.0;";
                        sb.append(typeString);
                        i += 1000;
                    }
                }
                // 没有分号, 不做处理
                else{
                    String typeString = key + "_" + value + "^" + String.valueOf(i) + "~1.0;";
                    sb.append(typeString);
                    i += 1000;
                }
            }

        }//for

        this.textAttrs = sb.toString().substring(0, sb.length() - 1);

        System.out.println("---------------getBrandTextAttrs结束-----------------------------------------------");

        return this.textAttrs;
    }


    public Map getOneIndex(String op){
        publicMap.put("text_attrs", this.textAttrs);
        Map<String, Object> reqDoc = new TreeMap<String, Object>();
        reqDoc.put("op", op);
        reqDoc.put("dataFields", publicMap);
        return reqDoc;
    }


    public JSONObject makesendInfo(List<Map> mapList){
        this.json = makeParam(mapList, indexName);
        return this.json;
    }


//    public JSONObject getAllSendJson(String indexName, String op) {
//        publicMap.put("text_attrs", this.textAttrs);
//
//        System.out.println("------------------------下面是publicMap里面的内容------------------------");
//        for (String k:publicMap.keySet()) {
//            System.out.println(k + ":" + publicMap.get(k));
//        }
//        System.out.println("------------------------publicMap里面结束------------------------");
//        /**
//         * reqDoc 字段中的一个文档
//         */
//        Map<String, Object> reqDoc = new TreeMap<String, Object>();
//        reqDoc.put("op", op);
//        reqDoc.put("dataFields", publicMap);
//
//        /**
//         * reqDoc 字段中的一个文档 拼接成列表
//         */
//        List<Map> mapList = new ArrayList<Map>();
//        mapList.add(reqDoc);
//
//        this.json = makeParam(mapList, indexName);
//
//        return this.json;
//    }


    private JSONObject makeParam(List<Map> mapList, String indexName) {
        Map<String, Object> sendMap = new TreeMap<String, Object>();
        sendMap.put("index", indexName);
        sendMap.put("timeout", 10);
        sendMap.put("reqDocs", mapList);
        JSONObject json = new JSONObject(sendMap);
        return json;
    }
}
