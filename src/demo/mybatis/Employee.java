/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Employee
 * Author:   sun2
 * Date:     2018/4/14 17:32
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package demo.mybatis;

/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/4/14 17:32
 * Description:  
 */
public class Employee {

    private Integer FID;

    private String FName;
    private Double FAge;
    public Integer getFID() {
        return FID;
    }

    public void setFID(Integer FID) {
        this.FID = FID;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public Double getFAge() {
        return FAge;
    }

    public void setFAge(Double FAge) {
        this.FAge = FAge;
    }

}
