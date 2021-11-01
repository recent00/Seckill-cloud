package com.scut.exception;

import com.scut.common.RespBean;
import com.scut.common.RespBeanEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * @RestControllerAdvice:springboot异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e) {
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if(e instanceof DAOException) {
            return RespBean.error(RespBeanEnum.ERROR,e.getMessage());
        }else if(e instanceof ServiceException) {
            return RespBean.error(RespBeanEnum.ERROR,e.getMessage());
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
