package com.scut.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    //秒杀
    //秒杀模块5005xx
    EMPTY_STOCK(500500, "库存不足"),
    REPEATE_ERROR(500501, "该商品每人限购一件"),
    DEAL_RECORD(500502,"订单处理中，请稍后。。。"),
    RECORD_FAIL(500503,"下单失败"),
    ACCESS_LIMIT_REAHCED(500504, "访问过于频繁，请稍后再试"),
    ;

    private final Integer code;
    private final String message;
}
