package netease.beauty.sendindex;

import org.json.JSONObject;

import java.io.*;
import java.net.URLConnection;
import java.util.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
//import net.sf.json.JSONObject;


/**
 * Created by hzqianwei on 2016/7/20.
 *
 */

public class SegmentTest {
    public static void main(String[] args) {

        Map<String, Object> map = new TreeMap<String, Object>();

        Map<String, String> testAtt = new TreeMap<String, String>();
        testAtt.put("KeyBrandId", String.valueOf(TestData.id));
        testAtt.put("KeyBrandCh", "abc");
        testAtt.put("KeyBrandEn", "abc");
        testAtt.put("KeyBrandForeign", "abc");
        testAtt.put("KeyBrandAlias", "abc");
        testAtt.put("KeyBrandRegion", "abc");
        testAtt.put("KeyBrandCompany", "abc");

        map.put("id", 1);
        map.put("score", 1.0);
        map.put("brief_text", "abc");
        map.put("official_website", "abc");
        map.put("description", "这是描述");
        map.put("search_tags", "这是测试数据");
        map.put("bcreate_time", 12345);
        map.put("status", 0);


        /**
         * -----------------------------------------------------------------------
         */
        Segment s = new Segment(map, testAtt, "beauty_brand");
        s.getBrandTextAttrs();
        Map m = s.getOneIndex("ADD");
        List<Map> l = new ArrayList<Map>();
        l.add(m);
        l.add(m);
        JSONObject json = s.makesendInfo(l);
        System.out.println(json);


        try{
            HttpSend hs = new HttpSend();
            String ss = hs.sendPost("http://app-71.photo.163.org:30001/id/service/pushData?recover=0", json.toString());
            JSONObject result = new JSONObject(ss);


            System.out.println(ss);
            System.out.println(result.get("code"));
            if ("200".equals(result.get("code").toString())) {
                System.out.println(result.get("code"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }



//        System.out.print("------------");
        //System.out.print(r);

//        JSONObject json = new JSONObject(map);
//
//        System.out.println(json);
//
//        /**
//         * reqDoc 字段中的一个文档
//         */
//        Map<String, Object> reqDoc = new TreeMap<String, Object>();
//        reqDoc.put("op", "ADD");
//        reqDoc.put("dataFields", map);
//
//        List<Map> mapList = new ArrayList<Map>();
//        mapList.add(reqDoc);
//
//        JSONObject j = makeParam(mapList);
//
//        System.out.println(j);
    }

    public static JSONObject makeParam(List<Map> mapList) {
        Map<String, Object> sendMap = new TreeMap<String, Object>();
        sendMap.put("index", "USER");
        sendMap.put("timeout", "10");
        sendMap.put("reqDocs", mapList);
        JSONObject json = new JSONObject(sendMap);
        return json;
    }


    /*
    *
    * {
	“index”:”USER”,
	“timeout”:10,
	“reqDocs”:[
		{
			“op”:”ADD”,
			“version”:“ver”,(可选)
			“dataFields”: {
				“field1”:123,
				“field2”:true,
				“field3”:”abc”,
			}
		},
		{
			“op”:”UPDATE”,
			“fields”: {
				“field1”:234,
				“field2”:false,
				“field3”:”abc”,
			}
		]
	}
}

    *
    *
    * */

    /**
     *
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}

/**
 * 1)id：唯一标示实体的id
 3)score：综合静态分
 4)brief_text：一句话简介
 5)official_website：官网地址
 6)description：详细描述
 7)search_tags：搜索标签
 8)bcreate_time：品牌创建时间
 9)status:当前实体的状态
 */

class TestData{
    public static int id = 5326;
    public static String brandNameCn = "奥尔滨";
    public static String brandNameEn = "albion";
    public static String brandNameJp = "アルビオン";
    public static String otherName = "奥比虹;奥之美";
    public static String campany = "Albion 奥比虹集团";
    public static String country = "日本";
    public static int date_ = 1212122;
    public static String oneSentence = "澳尔滨（ALBION）是日本高级化妆品的象征。1956年诞生于日本东京，它的出现震撼了日本化妆品界。";
    public static String officalURL = "http://www.albion-cn.com/";
    public static String detail = "IGNIS英格妮斯品牌；1998年，ANNASUI化妆品进入市场，之后又与";
}



/**

 public static String sendPost(String url,String param)
  {
  String result="";
  try{
  URL httpurl = new URL(url);
  HttpURLConnection httpConn = (HttpURLConnection)httpurl.openConnection(); 
  httpConn.setDoOutput(true);
  httpConn.setDoInput(true);
  PrintWriter out = new PrintWriter(httpConn.getOutputStream());
  out.print(param);
  out.flush();
  out.close();
  BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
  String line;
  while ((line = in.readLine())!= null)
  {
  result += line;
  }
  in.close();
  }catch(Exception e){
  System.out.println("Helloword！"+e);
  }
  return result;
  }



 */

class TestData2 {
    public static int id = 5326;
    public static String KeyBrandId = "1549";
    public static String KeyBrandCh = "albion";
    public static String KeyBrandEn = "アルビオン";
    public static String KeyBrandForeign = "奥比虹;奥之美";
    public static String KeyBrandAlias = "Albion 奥比虹集团";
    public static String KeyBrandSearchName = "日本";
    public static String KeyBrandRegion = "1956-00-00";
    public static String KeyProCh = "澳尔滨（ALBION）是日本高级化妆品的象征。1956年诞生于日本东京，它的出现震撼了日本化妆品界。";
    public static String officalURL = "http://www.albion-cn.com/";
    public static String detail = "";
}

/**
 1）id：唯一标示实体的产品id
 2）text_attrs：存放一系列短文本的字段
 KeyBrandId:品牌ID
 KeyBrandCh:品牌名（中文名）
 KeyBrandEn:品牌外文名（英文名）
 KeyBrandForeign:品牌外文名(其它)
 KeyBrandAlias:品牌别名
 KeyBrandSearchName:品牌搜索名
 KeyBrandRegion:品牌所属地区
 KeyBrandCompany:品牌所属公司
 KeyProCh:产品名（中文名）
 KeyProEn:产品外文名1（英文名）
 KeyProForeign:产品外文名2
 KeyProAlias:产品别名
 KeyProSearchName:产品搜索名
 KeyProAttr:产品属性
 KeyProCategory：产品分类
 KeySkuName:SKU名
 KeySkuSearchName:SKU搜索别名
 KeyColorName：色号名称
 KeyColorAlias：色号别名
 3）price：价格
 4）score：综合静态分
 5）search_tags：搜索标签
 6）status：当前实体的状态，对应数据库中的status字段


 1549
 肌肤之钥
 亮采柔肤粉
 CLE DE PEAU BEAUTE
 クレ・ド・ポー ボーテ
 LUMINIZING FACE ENHANCER

 14
 想你色

 CP1


 提亮光泽


 450人民币



 */

class XinDe{
    public static int id = 1;
    public static int KeyProID = 10087;
    public static String KeyProCh = "Guerlain娇兰金钻保湿粉底液";
    public static String KeyProEn = "PARURE AQUA FOUNDATION";
    public static String KeyPrice = "300+RMB";
    public static String KeyPurPlace = "韩国免税店";
    public static String KeyAttitude = "值得回购";
    public static int KeyUserID = 334455;
    public static String KeyUserName = "殷笑影";
    public static String KeySkin = "干性肤质";
    public static String user_age = "用户年龄";
    public static String search_tags = "科学护肤";
    public static String  des = "好像很少有博主推荐娇兰家的粉底液，但是它家的粉底真心好用，是很轻薄细腻的质地，上脸有些像半雾面装，遮瑕能力我觉得够了，细小的瑕疵都可以遮掉，痘痘啊什么的不太行，毕竟是轻薄的粉底液，遮瑕的工作还是交给专门的遮瑕工具吧哈哈~\n" +
            "    我是大干皮，我很喜欢在夏天用它，冬天用就太干了，起皮起的想一头扎进水里。每次用都不需要挤出太多，一丢丢就可以啦，但是这个有个好处是就算用太多就不会感觉皮肤死白，（也可能我当时晒得太黑了，怎么抹都抹不白？？）反正就是推荐你们啦，四星。这支我从韩国免税店买哒，300+软妹币~~\n";
}

/**
 *
 *
 id:1
 text_attrs:
 KeyProID:10087
 KeyProCh:Guerlain娇兰金钻保湿粉底液
 KeyProEn_PARURE AQUA FOUNDATION
 KeyPrice:300+RMB
 KeyPurPlace:韩国免税店


 KeyUserName:殷笑影


 search_tags:科学护肤
 des:好像很少有博主推荐娇兰家的粉底液，但是它家的粉底真心好用，是很轻薄细腻的质地，上脸有些像半雾面装，遮瑕能力我觉得够了，细小的瑕疵都可以遮掉，痘痘啊什么的不太行，毕竟是轻薄的粉底液，遮瑕的工作还是交给专门的遮瑕工具吧哈哈~
 我是大干皮，我很喜欢在夏天用它，冬天用就太干了，起皮起的想一头扎进水里。每次用都不需要挤出太多，一丢丢就可以啦，但是这个有个好处是就算用太多就不会感觉皮肤死白，（也可能我当时晒得太黑了，怎么抹都抹不白？？）反正就是推荐你们啦，四星。这支我从韩国免税店买哒，300+软妹币~~
 score:348347


 Guerlain娇兰金钻保湿粉底液

 "好像很少有博主推荐娇兰家的粉底液，但是它家的粉底真心好用，是很轻薄细腻的质地，上脸有些像半雾面装，遮瑕能力我觉得够了，细小的瑕疵都可以遮掉，痘痘啊什么的不太行，毕竟是轻薄的粉底液，遮瑕的工作还是交给专门的遮瑕工具吧哈哈~
 我是大干皮，我很喜欢在夏天用它，冬天用就太干了，起皮起的想一头扎进水里。每次用都不需要挤出太多，一丢丢就可以啦，但是这个有个好处是就算用太多就不会感觉皮肤死白，（也可能我当时晒得太黑了，怎么抹都抹不白？？）反正就是推荐你们啦，四星。这支我从韩国免税店买哒，300+软妹币~~"

 300+RMB

 韩国免税店

 值得回购

 CP2

 殷笑影

 20

 干性肤质

 科学护肤


 */