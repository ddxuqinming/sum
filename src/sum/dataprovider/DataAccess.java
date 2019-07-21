/**
 * Copyright (C), 2015-2018, sum
 * Author:   xqm
 * Date:     2018/3/17 18:02
 * Description: 数据库访问helper
 */
package sum.dataprovider;


import java.sql.*;

import java.util.LinkedHashMap;

import com.sun.javafx.image.BytePixelSetter;
import sum.business.FieldValueCollection;
import  sum.common.*;
import sum.dataprovider.datatable.*;

public class DataAccess {

    private String className;//"com.mysql.jdbc.Driver";
    private String url;// "jdbc:mysql://127.0.0.1:3306/db_librarySys?user=root&password=111&useUnicode=true&characterEncoding=utf-8";
    private String sqluser="";
    private String sqlpwd;
    private Connection conn = null;
    private  boolean blnbeginTrans=false;

    private  SQLCommand insertCommand;
    private  SQLCommand updateCommand;
    private  SQLCommand deleteCommand;

    public boolean printSql=false;
     public DataAccess(){

    }
     public DataAccess(String dbClassName,String dbUrl){
        className=dbClassName;
        url=dbUrl;
    }
     public void createMySqlUrl(String ip,int port,String dbName,String user,String password,String ext){
            className="com.mysql.jdbc.Driver";
            if (ext==null)  ext="&useUnicode=true&characterEncoding=utf-8";
            url="jdbc:mysql://%s:%s/%s?user=%s&password=%s" + ext;
            url=String.format(url, ip,port,dbName,user,password);
     }
    public void createSqlServerUrl(String ip,int port,String dbName,String user,String password,String ext){
        className="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        url = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
       //jdbc:sqlserver://localhost:1433;DatabaseName=S
         url=String.format(url, ip,port,dbName);
        sqluser=user;
        sqlpwd=password;
    }
     public   String  getUrl(){
        return  url;
     }

     public   Connection getConnection()   {
         try {
                if ( conn == null ) {

                        Class.forName(className).newInstance();
                        if (sqluser!="")
                           conn = DriverManager.getConnection(url,sqluser,sqlpwd);
                        else
                            conn = DriverManager.getConnection(url);
                }
         } catch (Exception ex) {
               //throw new SQLException(ex.getMessage());
               throw new RuntimeException(ex);
         }
        return conn;
    }

    public ResultSet getResultSet(String sql )  {
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
        if (printSql) System.out.println(sql);
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
           if (printSql) System.out.println(sql);
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
    public int exeSql(SQLCommand command)  {
        if (printSql) System.out.println(command.sqlText);
        int result = -1;
        conn = getConnection();
        PreparedStatement stmt;
        try {
            stmt= conn.prepareStatement(command.sqlText,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            for(int i=0;i<command.FieldValues.size();i++){
                stmt.setObject(i+1,command.FieldValues.getValue(i));
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
    public int insert(SQLCommand insertCommand)  {
        if (printSql) System.out.println(insertCommand.sqlText);
        int result = -1;
        conn = getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(insertCommand.sqlText, Statement.RETURN_GENERATED_KEYS);
            for(int i=0;i<insertCommand.FieldValues.size();i++){
                stmt.setObject(i+1,insertCommand.FieldValues.getValue(i));

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
    public DBValue executeScalar(String sql)  {
        ResultSet rs = getResultSet(sql);
        DBValue dt=new DBValue();
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
    private LinkedHashMap  <String,Integer> getAllColumnName(ResultSet rs) {
        LinkedHashMap <String,Integer> names = new LinkedHashMap <String,Integer>(); //LinkedHashMap的key是按顺序添加，newHashMap是按Key排序
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


    public FieldValueCollection getOneRowHashtable(String sql)  {
        ResultSet rs = getResultSet(sql);
        FieldValueCollection hst=null;
        try {
            //获取数据库该表所有字段名
             LinkedHashMap  <String,Integer> fields= getAllColumnName(rs);
            if(rs.next()) {
                hst=new  FieldValueCollection();
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

    public DataTable getDataTable(String sql )  {
        ResultSet rs = getResultSet(sql);
        DataTable  dtb=new DataTable();
        try {
            //1加字段名称
             LinkedHashMap  <String,Integer> fields= getAllColumnName(rs);
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

    public int saveTable(DataTable dataTable,String tableName, String[] keyFields){
        if (dataTable.rows().size()==0) return  -1;
        insertCommand=null;
        updateCommand=null;
        deleteCommand=null;


        int autoID=0;
        for (int i=0;i< dataTable.rows().size();i++){
            if  (dataTable.rows(i).getDataRowState()== DataRowState.Added){
                autoID= insertRow(dataTable.rows(i),tableName);
                if (dataTable.AutoColumn!=""){
                    dataTable.rows(i).setValue(dataTable.AutoColumn,autoID);
                }
           }

            else  if  (dataTable.rows(i).getDataRowState()== DataRowState.Modified)
                updateRow(dataTable.rows(i),tableName,keyFields);
            else  if  (dataTable.rows(i).getDataRowState()== DataRowState.Deleted)
                deleteRow(dataTable.rows(i),tableName,keyFields);
         }
        return -1;

    }

    private int insertRow(DataRow dataRow, String tableName){
        if (insertCommand==null) {
           CommandBuilder  commandBuilder=new CommandBuilder(tableName,this);
            insertCommand=commandBuilder.createInsertCommand(dataRow);

        }else
        {
           String columnName ;
            for(int i=0;i<insertCommand.FieldValues.size();i++) {
                columnName= insertCommand.FieldValues.getKey(i);
                insertCommand.FieldValues.setValue(columnName,dataRow.getValue(columnName));
              }
        }
          return this.insert(insertCommand);

    }
    private int updateRow(DataRow dataRow, String tableName, String[] keyFields){
        if (updateCommand==null) {
            CommandBuilder  commandBuilder=new CommandBuilder(tableName,this);
            updateCommand=commandBuilder.createUpdateCommand(dataRow,keyFields);
        }else
        {
            String columnName ;
            for(int i=0;i<updateCommand.FieldValues.size();i++) {
                columnName= updateCommand.FieldValues.getKey(i);
                updateCommand.FieldValues.setValue(columnName,dataRow.getValue(columnName));
            }
        }
        return this.exeSql(updateCommand);

    }
    private int deleteRow(DataRow dataRow, String tableName, String[] keyFields){
        if (deleteCommand==null) {
            CommandBuilder  commandBuilder=new CommandBuilder(tableName,this);
            deleteCommand=commandBuilder.createDeleteCommand(dataRow,keyFields);
        }else
        {
            String columnName ;
            for(int i=0;i<deleteCommand.FieldValues.size();i++) {
                columnName= deleteCommand.FieldValues.getKey(i);
                deleteCommand.FieldValues.setValue(columnName,dataRow.getValue(columnName));
            }
        }
        return this.exeSql(deleteCommand);

    }
    public interface ResultSetHandler<T>{
        T handle(ResultSet rs) throws SQLException;
    }
     

}
