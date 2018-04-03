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
    private String url;// "jdbc:mysql://127.0.0.1:3306/db_librarySys?user=root&password=111&useUnicode=true&characterEncoding=utf-8";
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
    if (ext==null)  ext="&useUnicode=true&characterEncoding=utf-8";
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
               //throw new SQLException(ex.getMessage());
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

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return rs;
    }

    public  void  beginTrans(){
        blnbeginTrans=true;
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

   }
    public  void  commitTrans(){
        blnbeginTrans=false;
        try {
            this.conn.commit();
            this.conn.setAutoCommit(true);
        } catch (SQLException ex) {
            blnbeginTrans=false;
            throw new RuntimeException(ex);
        }

    }
    public  void  rollbackTrans(){
        blnbeginTrans=false;
        try {
            this.conn.rollback();
            this.conn.setAutoCommit(true);
        } catch (SQLException ex) {
            blnbeginTrans=false;
            throw new RuntimeException(ex);
        }

    }
    /* *
     * 修改，删除语句,返回影响数
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

        }catch (SQLException ex){

            throw new RuntimeException(ex);
        }
        return result;
    }

    /* *
      * 修改，删除语句,返回影响数
    */
    public int exeSql(String sql,Object[] params)  {
           int result = -1;
            conn = getConnection();
            PreparedStatement stmt;
            try {
                stmt= conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                 for(int i=0;i<params.length;i++){
                     stmt.setObject(i+1,params[i]);
                }
                result =  stmt.executeUpdate();
                stmt.close();
                stmt = null;

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        return result;
    }
    /* *
     * 返回自动递增列值
     */
    public int insert(String sql,Object... params)  {
        int result = -1;
        conn = getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for(int i=0;i<params.length;i++){
                stmt.setObject(i+1,params[i]);

            }
            stmt.executeUpdate();
            ResultSet rsKey = stmt.getGeneratedKeys();
            rsKey.next();
            result= rsKey.getInt(1);     //得到第一个键值

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }
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
        }catch (SQLException ex){

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
                names.put(rsmd.getColumnName(i+1), rsmd.getColumnType(i+1)  );

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
        }catch (SQLException ex){

            throw new RuntimeException(ex);
        }
    }


    public boolean isExist(String sql)  {
        ResultSet rs = getResultSet(sql);
        boolean b=false;
        try {
            if (rs.next())   b=  true;
            rs.close();

        }catch (SQLException ex){

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
                    drw.setDataRowState( DataRowState.Unchanged );
                }
            }
            rs.close();
            return dtb;
        }catch (SQLException ex){

            throw new RuntimeException(ex);
        }
    }

    public void close()   throws SQLException{
          if (conn != null)     conn.close();
     }
    public <T>  T getItem(String sql ,ResultSetHandler<T> rsh)  {
        try {
        ResultSet rs = getResultSet(sql);
        T result = null;
        result = rsh.handle(rs);
        rs.close();
        return result;
        }catch (SQLException ex){
              throw new RuntimeException(ex);
        }
    }

    public int saveTable(DataTable dataTable,String tableName){
        if (dataTable.rows().size()==0) return  -1;
        for (int i=0;i< dataTable.rows().size();i++){
            if  (dataTable.rows(i).getDataRowState()==DataRowState.Added)
                insertrow(dataTable.rows(i),tableName);
         }
        return -1;

    }
    private int insertrow( DataRow dataRow,String tableName){
          return -1;

    }

     public interface ResultSetHandler<T>{
         T handle(ResultSet rs) throws SQLException;
     }
     

}
