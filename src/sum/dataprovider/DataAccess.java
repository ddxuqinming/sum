/**
 * Copyright (C), 2015-2018, sum
 * Author:   xqm
 * Date:     2018/3/17 18:02
 * Description: 数据库访问helper
 */
package sum.dataprovider;

import jdk.nashorn.internal.ir.CatchNode;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import  sum.common.*;
public class DataAccess {

    private String className;//"com.mysql.jdbc.Driver";
    private String url;// "jdbc:mysql://127.0.0.1:3306/db_librarySys?user=root&password=111&useUnicode=true";
    private Connection conn = null;
    private  boolean blnbeginTrans=false;
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
         try {
                if ( conn == null ) {

                        Class.forName(className).newInstance();
                        conn = DriverManager.getConnection(url);
                }
         } catch (Exception ex) {
               //throw new Exception(ex.getMessage());
               throw new RuntimeException(ex);
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

    public  void  beginTrans(){
        blnbeginTrans=true;
        try {
            getConnection().setAutoCommit(false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

   }
    public  void  commitTrans(){
        blnbeginTrans=false;
        try {
            this.conn.commit();
            this.conn.setAutoCommit(true);
        } catch (Exception ex) {
            blnbeginTrans=false;
            throw new RuntimeException(ex);
        }

    }
    public  void  rollbackTrans(){
        blnbeginTrans=false;
        try {
            this.conn.rollback();
            this.conn.setAutoCommit(true);
        } catch (Exception ex) {
            blnbeginTrans=false;
            throw new RuntimeException(ex);
        }

    }
   /*
   *返回影响的行数
   */
    public int exeSql(String sql)  {
        int result = -1;
        try {
            conn = getConnection();

            Statement stmt = null;

            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            result = stmt.executeUpdate(sql);        //执行更新操作
            stmt.close();
            stmt = null;

        }catch (Exception ex){

            throw new RuntimeException(ex);
        }
        return result;
    }
    /**
     * 查询SQL语句，预期结果为一个String数组，返回结果
     *
     * @param sql
     *            所要执行的sql语句
     * @return 返回值为预期结果
     */
    public DataResult executeScalar(String sql)  {
        ResultSet rs = getResultSet(sql);
        DataResult dt=new DataResult();
        try {
            if (rs.next()) {
                dt.HasValue = true;
                dt.Value = rs.getObject(1);//从1开始
                dt.IsDBNull= (dt.Value==null);
            }else {
                dt.HasValue =  false;
             }
            rs.close();
            return dt;
        }catch (Exception ex){

            throw new RuntimeException(ex);
        }

    }

    /**
     * 根据结果集获取数据库中的所有列表名
     * @param rs
     * @return
     */
    private HashMap  <String,Integer> getAllColumnName(ResultSet rs) {
        HashMap <String,Integer> names = new HashMap <String,Integer>();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for(int i=0;i<rsmd.getColumnCount();i++) {
                names.put(   rsmd.getColumnName(i+1), rsmd.getColumnType(i+1)  );

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }


    public KeyValueListOf<String,Object> getOneRowHashtable(String sql)  {
        ResultSet rs = getResultSet(sql);
        KeyValueListOf<String,Object>  hst=null;
        try {
            //获取数据库该表所有字段名
            HashMap  <String,Integer> fields= getAllColumnName(rs);
            if(rs.next()) {
                hst=new KeyValueListOf<>();
                for(String name:fields.keySet()) {
                    hst.add(name,  rs.getObject(name) );
                }
            }
            rs.close();
            return hst;
        }catch (Exception ex){

            throw new RuntimeException(ex);
        }

    }
    public boolean isExist(String sql)  {
        ResultSet rs = getResultSet(sql);
        boolean b=false;
        try {
            if (rs.next())   b=  true;
            rs.close();

        }catch (Exception ex){

            throw new RuntimeException(ex);
        }

         return b;
    }

    public  DataTable getDataTable(String sql)  {
        ResultSet rs = getResultSet(sql);
        DataTable  dtb=new DataTable();
        try {
            //1加字段名称
            HashMap  <String,Integer> fields= getAllColumnName(rs);
            for(String name:fields.keySet()) {
                DataColumn dcl=new DataColumn(name,name,fields.get(name) );
                dtb.columns().add( name,dcl);
            }
            //2加数据
            while (rs.next()) {
                DataRow drw=dtb.newRow();
                for(String name:fields.keySet()) {

                     drw.Add(name,rs.getObject(name) );
                }
            }
            rs.close();
            return dtb;
        }catch (Exception ex){

            throw new RuntimeException(ex);
        }
    }

    public void close()   throws Exception{
          if (conn != null)     conn.close();
     }
}
