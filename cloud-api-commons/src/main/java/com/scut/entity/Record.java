package com.scut.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Record {

    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer state;

    private Date createTime;
}
