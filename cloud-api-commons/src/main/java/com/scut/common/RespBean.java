package com.scut.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {

    private long code;//状态码

    private String message;//信息

    private Object obj;

    /**
     * 功能描述: 成功返回结果
     *
     * @param:
     * @return:
     */
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),null);
    }

    /**
     * 功能描述: 成功返回结果
     *
     * @param:
     * @return:
     */
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBean.success().getMessage(),obj);
    }


    /**
     * 功能描述: 失败返回结果
     *
     * @param:
     * @return:
     *
     */
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),null);
    }


    /**
     * 功能描述: 失败返回结果
     *
     * @param:
     * @return:
     *
     */
    public static RespBean error(RespBeanEnum respBeanEnum,Object obj){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),obj);
    }
}
