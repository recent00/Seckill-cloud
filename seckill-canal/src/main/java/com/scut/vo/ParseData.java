package com.scut.vo;

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
}
