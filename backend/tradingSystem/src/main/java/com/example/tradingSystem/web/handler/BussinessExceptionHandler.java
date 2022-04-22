package com.example.tradingSystem.web.handler;

import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class BussinessExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(BussinessException.class)
	public JsonResult handleBusinessException(BussinessException ex){
		JsonResult JsonResult = new JsonResult();
		JsonResult.error(ex.getCode(), ex.getMsg());
		return JsonResult;
	}

	@ExceptionHandler(Exception.class)
	public JsonResult handleException(Exception ex){
		log.error(ex.getMessage(), ex);
		return new JsonResult().error();
	}
	
}
