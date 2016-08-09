package netease.beauty.mysql;

import netease.beauty.pub.SeDemo;
import netease.beauty.sendindex.HttpSend;
import netease.beauty.sendindex.Segment;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hzqianwei on 2016/7/25.
 *
 */

public class SelectInfo {

    private String postUrl = "http://app-71.photo.163.org:30001/id/service/pushData?recover=0";

    /**
     * JDBC连接读取发送数据
     */
    public void selectInfo(String tableName, String indexName) {

        // mysql -h10.122.180.141 -P6000 -ubeauty_test -pbeauty_test
        String user = "beauty_mirror";
        String password = "beauty_mirror";
        String url = "jdbc:mysql://10.164.174.53:6000/beauty-mirror?characterEncoding=utf8";

        String selectSqlString = "SELECT * FROM " + tableName;

        ResultSet selectRes = null;
        Connection con = null;                                      //定义一个MYSQL链接对象
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();   //MYSQL驱动
            con = DriverManager.getConnection(url, user, password); //连接MYSQL
            System.out.println("connect success");

            stmt = con.createStatement();
            selectRes = stmt.executeQuery(selectSqlString);         // selectRes是从数据库取出的结果的第一条

            System.out.println("开始for循环");

            List<Map> listMap = new ArrayList<Map>();
            HttpSend hs = new HttpSend();
            Segment seg = new Segment(indexName);

            int i = 0;
            int send = 0;
            while (selectRes.next()) {
                Map m = dealWithResult(tableName, selectRes, con, indexName);
                listMap.add(m);

                System.out.println("-----------------------------------处理完了" + String.valueOf(++i) +"条------------------------------------------------");
                System.out.println();
                System.out.println();
                Thread.sleep(100);

                send ++;
                if (send == 25){
                    System.out.println(send);
                    send(seg, listMap, hs, i, indexName);
                    listMap.clear();
                    send = 0;
                }
            }

            send(seg, listMap, hs, -1, indexName);
            listMap.clear();


        } catch (Exception e) {
            System.out.print("MYSQL ERROR:" + e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                if (selectRes != null)
                    selectRes.close();
                if (stmt != null)
                    stmt.close();
                if (con != null)
                    con.close();
            }catch (Exception e) {System.out.print("Close ERROR:" + e.getMessage());}
        }
    }

    /**
     * send the data
     * @param seg
     * @param listMap
     * @param hs
     * @param index
     */
    private void send(Segment seg, List<Map> listMap, HttpSend hs,int index, String indexName){
        JSONObject json = seg.makesendInfo(listMap);
        try{
            String ss = hs.sendPost(postUrl, json.toString());
            JSONObject result = new JSONObject(ss);
            System.out.println(ss);
            if (!"200".equals(result.get("code").toString())) {
                SeDemo.toFile(indexName + String.valueOf(index), json);
                System.out.println("error in index:" + String.valueOf(index));
            }
            }catch (Exception e) {
                e.printStackTrace();
                SeDemo.toFile(indexName + String.valueOf(index), json);
            }
    }


    /**
     * 任务分发
     */
    private Map dealWithResult(String tableName, ResultSet selectRes, Connection con, String indexName) throws Exception{
        if (tableName.equals("Beauty_ProductBrand")) {
            return makeBrandMap(selectRes, con, indexName);
        }else if (tableName.equals("Beauty_Product")) {
            return makeProductMap(selectRes, con, indexName);
        }else if (tableName.equals("Beauty_Note")) {
            return makeNoteMap(selectRes, con, indexName);
        }else if (tableName.equals("List")) {
            makeListMap(selectRes, con, indexName);
        }
        return null;
    }

    /**
     * 产品表的处理
      */
    private Map makeProductMap(ResultSet selectRes, Connection con, String indexName) throws Exception{

        Statement stmt = con.createStatement();

        Map<String, Object> publicMap = new TreeMap<String, Object>();
        Map<String, String> textAtt = new TreeMap<String, String>();

        // 产品id
        int id = selectRes.getInt("Id");
        publicMap.put("id", id);

        String KeyBrandId = selectRes.getString("BrandId");
        textAtt.put("KeyBrandId", KeyBrandId);
        System.out.print(KeyBrandId);

        /**
         * 品牌表部分
         */
        try {
            ResultSet brandRes = stmt.executeQuery("select * from Beauty_ProductBrand where Id=" + KeyBrandId);
            brandRes.next();
            System.out.println("开始brandRes");
            String KeyBrandEn = brandRes.getString("Name");             // KeyBrandEn:品牌外文名（英文名）
            textAtt.put("KeyBrandEn", KeyBrandEn.trim());

            String KeyBrandCh = brandRes.getString("ZhName");           // KeyBrandCh:品牌名（中文名）
            textAtt.put("KeyBrandCh", KeyBrandCh.trim());

            String KeyBrandForeign = brandRes.getString("OriginName");// KeyBrandForeign:品牌外文名(其它)
            textAtt.put("KeyBrandForeign", KeyBrandForeign.trim());

            String KeyBrandAlias = brandRes.getString("AliasName");     // KeyBrandAlias:品牌别名                                AliasName
            textAtt.put("KeyBrandAlias", KeyBrandAlias.trim());

            String KeyBrandSearchName = brandRes.getString("KeyWords"); // KeyBrandSearchName:品牌搜索名
            textAtt.put("KeyBrandSearchName", KeyBrandSearchName.trim());

            String KeyBrandRegion = brandRes.getString("Address");      // KeyBrandRegion:品牌所属地区
            textAtt.put("KeyBrandRegion", KeyBrandRegion.trim());

            /**
             * 公司表
             */
            String companyId = brandRes.getString("VendorId");
            ResultSet companyRes = stmt.executeQuery("select ZhName from Beauty_ProductVendor where Id = " + companyId);
            if (companyRes.next()){
                companyRes.last();
                String KeyBrandCompany = companyRes.getString("ZhName");          // KeyBrandCompany:品牌所属公司
                textAtt.put("KeyBrandCompany", KeyBrandCompany.trim());
            }
        }catch (Exception e) {e.printStackTrace();System.out.println("产品索引, 品牌表异常");}

        String KeyProCh = selectRes.getString("ZhName");                // KeyProCh:产品名（中文名）                              ZhName
        textAtt.put("KeyProCh", KeyProCh.trim());

        String KeyProEn = selectRes.getString("Name");                  // KeyProEn:产品外文名1（英文名）                                  Name
        textAtt.put("KeyProEn", KeyProEn.trim());

        String KeyProForeign = selectRes.getString("OriginName");    // KeyProForeign:产品外文名2
        textAtt.put("KeyProForeign", KeyProForeign.trim());

        String KeyProAlias = selectRes.getString("AliasName");          // KeyProAlias:产品别名                                   AliasName
        textAtt.put("KeyProAlias", KeyProAlias.trim());

        String KeyProSearchName = selectRes.getString("Keywords");      // KeyProSearchName:产品搜索名Keywords
        textAtt.put("KeyProSearchName", KeyProSearchName.trim());


        /**
         * 产品属性
         */
        try{
            ResultSet r = stmt.executeQuery("select * from Beauty_ProductNormalProperty where ProductId=" + id);// KeyProAttr:产品属性
            if (r.next()){
                String KeyProAttr = r.getString("Value");
                textAtt.put("KeyProAttr", KeyProAttr.trim());
            }
        }catch (Exception e){e.printStackTrace();System.out.println("产品索引, 产品属性异常");}

        /**
         * 类别表部分
         */
        try{
            String KeyProCategoryId = selectRes.getString("CategoryId");          // KeyProCategory：产品分类
            ResultSet CategoryRes = stmt.executeQuery("select * from Beauty_ProductCategory where Id=" + KeyProCategoryId);
            CategoryRes.last();
            String KeyProCategory = CategoryRes.getString("Name");
            textAtt.put("KeyProCategory", KeyProCategory.trim());

        }catch (Exception e){e.printStackTrace();System.out.println("产品索引, 产品类别异常");}

        /**
         * SKU表部分
         */
        try{
            // KeySkuName:SKU名
            ResultSet skuRes = stmt.executeQuery("select Price,sort,Keywords from Beauty_ProductSKU where ProductId=" + id);
            // KeySkuSearchName:SKU搜索别名
            if(skuRes.next()){
                long minSort = skuRes.getLong("sort");
                String KeySkuSearchName = skuRes.getString("Keywords");
                int price = skuRes.getInt("Price");
                while(skuRes.next()) {
                    long t =  skuRes.getLong("sort");
                    if(t < minSort){
                        minSort = t;
                        KeySkuSearchName = skuRes.getString("Keywords");
                        price = skuRes.getInt("Price");
                    }
                }
                textAtt.put("KeySkuSearchName", KeySkuSearchName);
                publicMap.put("price", price);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("产品索引, SKU表部分异常");
        }



        // KeyColorName：色号名称
        // KeyColorAlias：色号别名
        // 3）price：价格

        // 4）score：综合静态分
        publicMap.put("score", 1);

        String status = selectRes.getString("Status"); // 6）status：当前实体的状态，对应数据库中的status字段
        publicMap.put("status", status.trim());

        System.out.println("-----------------------产品信息开始-------------------------------------------------------------------------------------------");
        for (String key:publicMap.keySet()) {
            System.out.println(key + ": " + publicMap.get(key));
        }
        for (String key:textAtt.keySet()) {
            System.out.println(key + ": " + textAtt.get(key));
        }
        System.out.println("-----------------------信息结束-------------------------------------------------------------------------------------------");

        /**
         * KeyColorAlias：色号别名
         * KeyColorName：色号名称
         */
        stmt.close();
        return makeOneIndexMap(publicMap, textAtt, indexName);
    }

    /**
     * 品牌表的处理
     */
    private Map makeBrandMap(ResultSet selectRes, Connection con, String indexName) throws Exception{

        Statement stmt = con.createStatement();

        Map<String, Object> publicMap = new TreeMap<String, Object>();
        Map<String, String> textAtt = new TreeMap<String, String>();

        int id = selectRes.getInt("Id"); // 1)id：唯一标示实体的id
        publicMap.put("id", id);

        // 2)text_attrs：存放一系列短文本的字段
        String KeyBrandId = selectRes.getString("Id");      // KeyBrandId:品牌ID
        textAtt.put("KeyBrandId", KeyBrandId);
        String KeyBrandCh = selectRes.getString("ZhName");  // KeyBrandCh:品牌名（中文名）
        textAtt.put("KeyBrandCh", KeyBrandCh.trim());
        String KeyBrandEn = selectRes.getString("Name");    // KeyBrandEn:品牌外文名（英文名）
        textAtt.put("KeyBrandEn", KeyBrandEn.trim());
        String KeyBrandForeign = selectRes.getString("OriginName"); // KeyBrandForeign:品牌外文名(其它)
        textAtt.put("KeyBrandForeign", KeyBrandForeign.trim());
        String KeyBrandAlias = selectRes.getString("AliasName");    // KeyBrandAlias:品牌别名
        textAtt.put("KeyBrandAlias", KeyBrandAlias.trim());
        String KeyBrandSearchName = selectRes.getString("KeyWords");    // KeyBrandSearchName:品牌搜索名
        textAtt.put("KeyBrandSearchName", KeyBrandSearchName.trim());
        String BrandRegion = selectRes.getString("Address");            // BrandRegion:品牌所属地区
        textAtt.put("BrandRegion", BrandRegion.trim());

        // KeyBrandCompany:品牌所属公司
        try{
            String VendorId = selectRes.getString("VendorId"); //KeyBrandCompany:品牌所属公司
            ResultSet companyRes = stmt.executeQuery("select ZhName from Beauty_ProductVendor where Id = " + VendorId);
            if (companyRes.next()) {
                String KeyBrandCompany = companyRes.getString("ZhName");
                System.out.println("KeyBrandCompany:"+KeyBrandCompany);
                textAtt.put("KeyBrandCompany", KeyBrandCompany.trim());
            }
        } catch(Exception e) {e.printStackTrace();}

        publicMap.put("score", 1.0);           //3)score：综合静态分

        String brief_text = selectRes.getString("Intro");           //Intro 4)brief_text：一句话简介
        publicMap.put("brief_text", brief_text);
        String official_website = selectRes.getString("Website");   // 5)official_website：官网地址
        publicMap.put("official_website", official_website);

        try{
            ResultSet productBrandExt = stmt.executeQuery("select Detail from Beauty_ProductBrandExt where Id=" + id);//Beauty_ProductBrandExt
            if (productBrandExt.next()) {
                String description = productBrandExt.getString("Detail");//6)description：详细描述
                textAtt.put("description", description.trim());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        long bcreate_time = selectRes.getLong("FoundingTime");   // 8)bcreate_time：品牌创建时间
        publicMap.put("bcreate_time", bcreate_time);
        int status = selectRes.getInt("Status");      // 9)status:当前实体的状态
        publicMap.put("status", status);

        System.out.println("-------------------品牌Map表信息开始----------------------------------------------------------");
        for (String k:publicMap.keySet()) {
            System.out.println(k+":"+publicMap.get(k));
        }
        for (String k:textAtt.keySet()) {
            System.out.println(k+":"+textAtt.get(k));
        }
        System.out.println("-------------------品牌Map表信息结束----------------------------------------------------------");
        stmt.close();
        /**
        * 发送去切割
         */
        return makeOneIndexMap(publicMap, textAtt, indexName);
    }

    /**
     * 清单的处理
     */
    private void makeListMap(ResultSet selectRes, Connection con, String indexName) throws Exception {

    }

    /**
     * 用户评心得的处理
     */
    private Map makeNoteMap(ResultSet selectRes, Connection con, String indexName) throws Exception {

        Statement stmt = con.createStatement();

        Map<String, Object> publicMap = new TreeMap<String, Object>();
        Map<String, String> textAtt = new TreeMap<String, String>();

        int id = selectRes.getInt("id"); // 1)id：唯一标示实体的id
        publicMap.put("id", id);

        // 2)text_attrs：存放一系列短文本的字段
        try{
            String KeyProID = null;
            String type = selectRes.getString("type"); // KeyProID:产品id
            String tempId = selectRes.getString("productid");

            // 确定productId
            if ("0".equals(type)) {
                KeyProID = tempId;
            }else {
                ResultSet r = stmt.executeQuery("select ProductSKUId from Beauty_ProductSKUProperty where Id = " + tempId);
                r.next();
                KeyProID = r.getString("ProductSKUId");
            }
            textAtt.put("KeyProID", KeyProID);

            ResultSet prod = stmt.executeQuery("select * from Beauty_Product where Id = " + KeyProID);

            if (prod.next()) {
                String KeyProCh = prod.getString("ZhName"); // KeyProCh:产品名（中文名）
                textAtt.put("KeyProCh", KeyProCh);

                String KeyProEn = prod.getString("Name"); // KeyProEn:产品外文名1（英文名）
                textAtt.put("KeyProEn", KeyProEn);

                String KeyProForeign = prod.getString("OriginName");// KeyProForeign:产品外文名2
                textAtt.put("KeyProForeign", KeyProForeign);

                String KeyProAlias = prod.getString("AliasName");// KeyProAlias:产品别名
                textAtt.put("KeyProAlias", KeyProAlias);

                String KeyProSearchName = prod.getString("Keywords");// KeyProSearchName:产品搜索名
                textAtt.put("KeyProSearchName", KeyProSearchName);
            }

        }catch (Exception e) {e.printStackTrace();System.out.println("心得部分, 产品异常");}


        String KeyPurPlace = selectRes.getString("buyaddr");  // KeyPurPlace:购入地
        textAtt.put("KeyPurPlace", KeyPurPlace);

        String KeyPrice = selectRes.getString("buyprice");      // KeyPrice:购入大体价格
        textAtt.put("KeyPrice", KeyPrice);

        String KeyAttitude = selectRes.getString("score");// KeyAttitude：使用态度
        textAtt.put("KeyAttitude", KeyAttitude);

        String KeyUserID = selectRes.getString("userid");// KeyUserID：用户ID

        textAtt.put("KeyUserID", KeyUserID);
        try{
            ResultSet userp = stmt.executeQuery("select * from Beauty_UserProfile where UserId = " + KeyUserID);

            if (userp.next()) {
                String KeyUserName =  userp.getString("UserName");              // KeyUserName：用户名
                textAtt.put("KeyUserName", KeyUserName);

                String KeySkin =  userp.getString("SkinType");              // KeySkin：肤质
                textAtt.put("KeySkin", KeySkin);
            }

            //String user_age = userp.getString("");              //5)user_age:用户年龄 long
            userp.close();
        }catch (Exception e){e.printStackTrace();System.out.println("心得部分, 用户属性异常");}



        try{
            ResultSet noteLabel = stmt.executeQuery("select * from Beauty_NoteLabel where noteid=" + id);
            noteLabel.last();
            String labelid = noteLabel.getString("labelid");             //KeyExpTag:心得标签    目前只有标签ID
            textAtt.put("KeyExpTag", labelid);
            noteLabel.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("用户心得标签异常");
        }

        Integer score = 0;        //   3)score：综合静态分
        publicMap.put("score", score);

        String des = selectRes.getString("content");// 4)des：正文
        publicMap.put("des", des);

        String status = selectRes.getString("status");
        publicMap.put("status", status);

        for (String k:textAtt.keySet()){
            System.out.println(k + ":" + textAtt.get(k));
        }
        System.out.println("---------==============================--------------------------------------------------------------------------------------");
        for (String k:publicMap.keySet()){
            System.out.println(k + ":" + publicMap.get(k));
        }

        //stmt.close();
        /**
         * 目前缺省字段 :
         * 5)user_age:用户年龄
         * KeyExpTag:心得标签    目前只有标签ID
         */
        stmt.close();

        return makeOneIndexMap(publicMap, textAtt, indexName);

    }

    /**
     * 制作json数据
     */
    private Map makeOneIndexMap(Map<String, Object> publicMap, Map<String, String> textAtt, String indexName) {
        Segment seg = new Segment(publicMap, textAtt, indexName);
        seg.getBrandTextAttrs();
        return seg.getOneIndex("ADD");
    }

    /**
     *
    private void makeJsonAndSend(Map<String, Object> publicMap, Map<String, String> textAtt, String indexName){
        Segment seg = new Segment(publicMap, textAtt, indexName);
        seg.getBrandTextAttrs();
        JSONObject jsonObject = seg.getAllSendJson(indexName,"ADD");
        System.out.println(jsonObject);
        HttpSend hs = new HttpSend();
        try{
            String r = hs.sendPost("http://172.17.3.216:30001/id/service/pushData", jsonObject);
            System.out.println("result-------------------------------------------------------------------");
            System.out.println(r);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
     *
     */
}