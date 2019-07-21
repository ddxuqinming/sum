/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: ItemManageBase
 * Author:   sun2
 * Date:     2019/7/19 20:49
 * Description: 业务管理类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.business;

import sum.dataprovider.CommandBuilder;
import sum.dataprovider.DBValue;
import sum.dataprovider.DataAccess;
import sum.dataprovider.SQLCommand;
import sum.dataprovider.datatable.DataColumn;
import sum.dataprovider.datatable.DataRow;
import sum.dataprovider.datatable.DataRowState;
import sum.dataprovider.datatable.DataTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2019/7/19 20:49
 * Description:  业务管理类
 */
public class ItemManageBase {
    DataAccess xudb=null;
    String  tableName;
    String  primaryKey ;
    List<ChildTableRelation> childTableRelations;
    public DataAccess getXudb() {
        return xudb;
    }
    public void setXudb(DataAccess xudb) {
        this.xudb = xudb;
    }
    public  ItemManageBase(String tableName,String keyField){
       this.tableName=tableName;
       this.primaryKey=keyField;


   }
    public  ItemBase getNewItem(){
        String sql= "select * from " + tableName + " where 1=0"  ;
        DataTable  dtb = this.getXudb().getDataTable(sql);
        ItemBase item =new ItemBase();
        for (int i=0;i<dtb.columns().size();i++){
            item.FieldValues().add(dtb.columns(i).getColumnName(),null);
        }
        //添加子表
        if (childTableRelations!=null )  {
            DataTable dtbChild;
            for (int i=0;i<childTableRelations.size();i++){
                ChildTableRelation child=childTableRelations.get(i);
                dtbChild =  onLoadChildDataTable(item,child.childName,child.dbChildTableName, "1=0");
                item.ChildTables().add(child.childName,dtbChild);
            }
        }
        return item;
    }
    public void addChildDBTable(String childName,String childForeginField,String dbChildTableName,String[] childKeyFields){
          ChildTableRelation child=new ChildTableRelation();
          child.parentFieldName=this.primaryKey;
          child.childName=childName;
          child.childForeginField =childForeginField;
          child.dbChildTableName=dbChildTableName;
          child.childKeyFields=childKeyFields;
          if (childTableRelations==null)
              childTableRelations= new ArrayList() ;
        childTableRelations.add(child);

    }
    public   DataTable onLoadChildDataTable(ItemBase item,String childName, String dbChildTableName,String where){
        String sql= "select * from " + dbChildTableName + " where "  +  where;
        DataTable dtb= this.getXudb().getDataTable(sql);
        dtb.setTableName(childName);
        return  dtb;
    }
    public  ItemBase getItem(Integer   Key){
       String where= primaryKey + "=" + Key;
       String sql= "select * from " + tableName + " where " +  where;
        DataTable  dtb = this.getXudb().getDataTable(sql);
        if ( dtb.rows().size()==0 ) return null;
        ItemBase item =new ItemBase();
        DataRow drwMain = dtb.rows(0);
        for (int i=0;i<dtb.columns().size();i++){
            item.FieldValues().add(dtb.columns(i).getColumnName(),drwMain.getValue(i));
        }
        //添加子表
        if (childTableRelations!=null )  {
            DataTable dtbChild;
            for (int i=0;i<childTableRelations.size();i++){
                ChildTableRelation child=childTableRelations.get(i);
                dtbChild =  onLoadChildDataTable(item,child.childName,child.dbChildTableName,  child.childForeginField + "=" + item.getValue(child.parentFieldName));
                 item.ChildTables().add(child.childName,dtbChild);
            }
        }


       return item;
    }



  public boolean add(ItemBase item){
        String sql= "select * from " + tableName + " where 1=0"  ;
        DataTable  dtb = this.getXudb().getDataTable(sql);
        DataRow drwNew = dtb.newRow();
        for (int i=0;i<item.FieldValues().size() ;i++){
            drwNew.setValue(item.FieldValues().getKey(i),item.FieldValues().getValue(i));
        }

       //1插入到数据库
        SQLCommand insertCommand;
        CommandBuilder commandBuilder=new CommandBuilder(this.tableName,this.getXudb());
        insertCommand=commandBuilder.createInsertCommand(drwNew);
       int autoid = this.getXudb().insert(insertCommand);
        //2返回自动递增列
        if (dtb.AutoColumn!="")
            item.FieldValues().setValue(dtb.AutoColumn,autoid);

       //3保存子表
       if (childTableRelations!=null )  {
          DataTable dtbChild;
          Integer parentID = Integer.parseInt( item.getValue(this.primaryKey).toString());
          if (parentID==0) throw new RuntimeException("主表" + primaryKey + "值为空,不能保存子表记录");
          for (int i=0;i<childTableRelations.size();i++){
              ChildTableRelation child=childTableRelations.get(i);
               this.saveChildTable(item.ChildTables().getValue(child.childName),child.dbChildTableName, child.childForeginField,parentID,child.childKeyFields);
          }
      }

        return  true;
    }

    public boolean update(ItemBase item){
        String where= primaryKey + "=" + item.FieldValues().getValue (primaryKey);
        String sql= "select * from " + tableName + " where " +  where;
        DataTable  dtb = this.getXudb().getDataTable(sql);
        if ( dtb.rows().size()==0 ) throw new RuntimeException("update:数据库中找不到对象值");
        DataRow drwItem = dtb.rows(0);
        for (int i=0;i<item.FieldValues().size() ;i++){
            drwItem.setValue(item.FieldValues().getKey(i),item.FieldValues().getValue(i));
        }

        //1保存到数据库
        SQLCommand updateCommand;
        CommandBuilder commandBuilder=new CommandBuilder(this.tableName,this.getXudb());
        String[] keyFields = new String[1];
        keyFields[0]=primaryKey  ;

        updateCommand=commandBuilder.createUpdateCommand(drwItem,keyFields);
        this.getXudb().exeSql (updateCommand);


        //3保存子表
        if (childTableRelations!=null )  {
            DataTable dtbChild;
            Integer parentID = Integer.parseInt( item.getValue(this.primaryKey).toString());
            for (int i=0;i<childTableRelations.size();i++){
                ChildTableRelation child=childTableRelations.get(i);
                this.saveChildTable(item.ChildTables().getValue(child.childName),child.dbChildTableName, child.childForeginField,parentID,child.childKeyFields);
            }
        }


         return  true;
    }

    private  void  saveChildTable(DataTable dtb, String dbtableName, String foreginField, int parentID,String[] childKeyFields){

        for (int i=0;i<dtb.rows().size();i++){
             dtb.rows(i).setValue(foreginField,parentID);
        }
        this.getXudb().saveTable(dtb,dbtableName,childKeyFields);
    }

    public boolean delete(ItemBase item){
        String where= primaryKey + "=" + item.FieldValues().getValue (primaryKey);
        String sql= "delete from " + tableName + " where " +  where;
        this.getXudb().exeSql(sql);
         return  true;
    }
}

//子表关系
class ChildTableRelation{
    public String parentFieldName;//如主表的FID
    public String childName;
    public String childForeginField;//如FMainID
    public String dbChildTableName;
    public String[] childKeyFields;//如 FAutoID
}
