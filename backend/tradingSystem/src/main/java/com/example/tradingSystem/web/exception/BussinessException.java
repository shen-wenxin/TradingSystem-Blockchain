package com.example.tradingSystem.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.tradingSystem.common.Status;


@Data
@EqualsAndHashCode(callSuper = false)
public class BussinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public BussinessException(int code) {
        this.code = code;
        this.msg = Status.getMessage(code);
    }

    public BussinessException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = Status.getMessage(code);
    }

    public BussinessException(String msg) {
        super(msg);
        this.code = Status.FAIL_EXCEPTION.code();
        this.msg = msg;
    }

    public BussinessException(int code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BussinessException(String msg, Throwable e) {
        super(msg, e);
        this.code = Status.FAIL_EXCEPTION.code();
        this.msg = msg;
    }

}
