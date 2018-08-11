/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: test
 * Author:   sun2
 * Date:     2018/3/28 19:54
 * Description: 测试类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.dataprovider;
import java.math.BigDecimal;
import java.sql.Types;
import java.sql.*;
import java.util.HashMap;
import  sum.common.*;
import java.util.*;



/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/3/28 19:54
 * Description:  测试类
 */
public class test {


    public static void main(String[] args)  {
        System.out.println("loading... xqm");
       new test().testDataAccess();

        List<Object> list = new ArrayList<Object>();
        list.add(new Integer(5));
        Object a = list.get(0);
    }

    private   void  testDataTable(){
       DataTable dtb=new DataTable();
        dtb.columns().add("FID","ID", java.sql.Types.TINYINT );
        dtb.columns().add("FName","姓名", Types.VARCHAR );
        DataRow drw=dtb.newRow();
        drw.Add("FID",1);
        drw.Add("FName","xqm");

        System.out.println("size=" + dtb.rows(0).getValue("FName"));


    }

    private   void  testDataAccess() {
        DataAccess xudb=new DataAccess();
        xudb.initMySqlUrl("localhost",3306,"xusoft","root","111",null);
       // System.out.println(xudb.getUrl());
       // xudb.beginTrans();
        int i= xudb.exeSql("update uemployee set FName='AA2' where  FName=?",  new Object[]{"7"});
      //  xudb.rollbackTrans();
         DataTable  dtb= xudb.getDataTable("select  FID,FName,FAge from uEmployee where FID=1");
       // dtb.rows(0).setValue("FName","这1");
       // dtb.rows(1).setValue("FName","这2");

      DataRow drw=  dtb.newRow();
       drw.setValue("FName","这3444");
      drw.setValue(2,"444");
      drw.delete();
       xudb.saveTable(dtb,"uEmployee",new String[]{"FID"});
        System.out.println("size=" + drw.getValue("FID"));


    }

}
