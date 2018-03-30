/**
 * Copyright (C), 2015-2018, sum
 * Author:   xqm
 * Date:     2018/3/17 18:02
 * Description: 数据库访问helper
 */
package sum.dataprovider;

import java.sql.*;
public class DataAccess {

    private String className;//"com.mysql.jdbc.Driver";
    private String url;// "jdbc:mysql://127.0.0.1:3306/db_librarySys?user=root&password=111&useUnicode=true";
    private Connection conn = null;
    public DataAccess(){

    }
    public DataAccess(String dbClassName,String dbUrl){
        className=dbClassName;
        url=dbUrl;
  }
public void initMySqlUrl(String ip,int port,String dbName,String user,String password,String ext){
    className="com.mysql.jdbc.Driver";
    url="jdbc:mysql://%s:%s/%s?user=%s&password=%s" + ext;
    url=String.format(url, ip,port,dbName,user,password);
}
public   String  getUrl(){
        return  url;
}

    public   Connection getConnection()   {
        if ( conn == null) {
            try {
                Class.forName(className).newInstance();
                conn = DriverManager.getConnection(url);

             } catch (Exception ex) {
                  //throw new Exception(ex.getMessage());
                 throw new RuntimeException(ex);
                }

        }
        return conn;
    }

    public ResultSet getResultSet(String sql)  {
        ResultSet rs = null;
        try {
            conn = getConnection();
            Statement stmt = null;
             stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
            stmt=null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return rs;
    }
   /*
   *返回影响的行数
   * */
    public int executeUpdate(String sql)  {
        int result = -1;
        try {


            conn = getConnection();
            Statement stmt = null;
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            result = stmt.executeUpdate(sql);        //执行更新操作
            stmt = null;
        }catch (Exception ex){

            throw new RuntimeException(ex);
        }
        return result;
    }
    public void close()   throws Exception{
          if (conn != null)     conn.close();
     }
}
