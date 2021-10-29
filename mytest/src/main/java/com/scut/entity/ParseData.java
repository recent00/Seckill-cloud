package com.scut.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseData<T> {
    private List<T> old;//老数据

    private List<T> data;//新数据

    private String database;//数据库

    private String table;//表

    private String type; //操作类型

    private boolean isDdl;//是否是ddl语句

    private String pkNames;//主键name

   /* public ParseData() {
    }

    public ParseData(List<T> old, List<T> data, String database, String table, String type, boolean isDdl, String pkNames) {
        this.old = old;
        this.data = data;
        this.database = database;
        this.table = table;
        this.type = type;
        this.isDdl = isDdl;
        this.pkNames = pkNames;
    }

    public List<T> getOld() {
        return old;
    }

    public void setOld(List<T> old) {
        this.old = old;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDdl() {
        return isDdl;
    }

    public void setDdl(boolean ddl) {
        isDdl = ddl;
    }

    public String getPkNames() {
        return pkNames;
    }

    public void setPkNames(String pkNames) {
        this.pkNames = pkNames;
    }*/
}
