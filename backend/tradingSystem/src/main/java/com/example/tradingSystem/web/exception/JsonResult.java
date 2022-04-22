package com.example.tradingSystem.web.exception;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.example.tradingSystem.common.Status;

@ApiModel(value="接口返回对象", description="接口返回对象")
@Slf4j
@Data
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 编码：0表示成功，其他值表示失败
     */
    @ApiModelProperty(value = "编码：0表示成功，其他值表示失败")
    private int code = 0;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "返回处理消息")
    private String msg = "success";
    /**
     * 响应数据
     */
    @ApiModelProperty(value = "返回数据对象")
    private T data;
    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();

    public JsonResult<T> ok(T data) {
        this.setData(data);
        return this;
    }

    public boolean success(){
        return code == 0 ? true : false;
    }

    public JsonResult success(String msg){
        this.msg =msg;
        this.code = 0;
        return this;
    }
    public JsonResult<T> error() {
        this.code = Status.FAIL_EXCEPTION.code();
        this.msg = Status.getMessage(this.code);
        return this;
    }

    public JsonResult<T> error(int code) {
        this.code = code;
        this.msg = Status.getMessage(this.code);
        return this;
    }

    public JsonResult<T> error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public JsonResult<T> error(String msg) {
        this.code = Status.FAIL_EXCEPTION.code();
        this.msg = msg;
        return this;
    }

}