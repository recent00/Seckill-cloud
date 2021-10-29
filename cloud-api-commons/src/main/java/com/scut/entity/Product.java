package com.scut.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {

    private Integer id;

    private String productName;

    private BigDecimal price;

    private Integer stock;

    private Integer versionId;
}
