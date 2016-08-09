package netease.beauty.mysql;

import netease.beauty.pub.SeDemo;

import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hzqianwei on 2016/7/24.
 */
public class MysqlTest {

    public static String url = "jdbc:mysql://127.0.0.1:3306/mysql";
    public static String user = "root";
    public static String password = "123456";
    public static String selectSqlString = "SELECT * FROM help_keyword";

    public static void main(String[] args) {

//        SelectInfo si = new SelectInfo();
//        si.selectInfo("mysql");

        Map<String, Object> m = new TreeMap<String, Object>();
        m.put("X", "X");
        SeDemo.toFile("test", m);
    }
}
