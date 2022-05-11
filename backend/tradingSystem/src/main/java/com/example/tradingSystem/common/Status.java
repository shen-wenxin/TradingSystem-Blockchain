package com.example.tradingSystem.common;

public enum Status {

    /***
     * 请求处理成功
     */
    OK(0, "操作成功"),

    /***
     * 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
     */
    WARN_PARTIAL_SUCCESS(101, "部分成功"),

    /***
     * 有潜在的性能问题
     */
    WARN_PERFORMANCE_ISSUE(102, "潜在的性能问题"),

    /***
     * 传入参数不对
     */
    FAIL_INVALID_PARAM(400, "请求参数不匹配"),

    /***
     * Token无效或已过期
     */
    FAIL_INVALID_TOKEN(401, "Token无效或已过期"),

    /***
     * 没有权限执行该操作
     */
    FAIL_NO_PERMISSION(403, "无权执行该操作"),

    /***
     * 请求资源不存在
     */
    FAIL_NOT_FOUND(404, "请求资源不存在"),

    /***
     * 数据校验不通过
     */
    FAIL_VALIDATION(405, "数据校验不通过"),

    /***
     * 操作执行失败
     */
    FAIL_OPERATION(406, "操作执行失败"),

    /***
     * 系统异常
     */
    FAIL_EXCEPTION(500, "系统异常"),

    //自定义异常以1000开头

    ACCOUNT_PASSWORD_ERROR(10004, "账号或密码错误"),

    ACCOUNT_DISABLE(10005, "账号已被停用"),

    CAPTCHA_ERROR(10006, "验证码不正确"),

    PASSWORD_ERROR(10007, "密码错误"),

    ACCOUNT_NOT_EXIST(10008, "账号不存在"),

    UPLOAD_FILE_EMPTY(10019, "上传文件不能为空"),

    OSS_UPLOAD_FILE_ERROR(10024, "阿里云上传文件失败"),

    SEND_SMS_ERROR(10025, "发送短信出错"),

    MAIL_TEMPLATE_NOT_EXISTS(10026, "邮件模板不存在"),

    JSON_FORMAT_ERROR(10030, "Json格式化出错"),

    SMS_CONFIG_ERROR(10031, "短信配置出错"),

    REPEAT_SUBMIT_ERROR(10029, "重复请求，请稍后再试"),

    JOB_ERROR(10028, "执行定时任务出错"),

    BLOCKCHAIN_SERVICE_FAILED(11000, "区块链服务异常"),

    

    USER_DATA_EXIST(11001, "用户数据已存在"),

    TRADE_SERVICE_FAILED(11002, "交易失败,详情请问系统管理员");

    private int code;
    private String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    /**
     * 根据消息值返回编码
     *
     * @param value
     * @return
     */
    public static int getCode(String value) {
        for (Status eu : Status.values()) {
            if (eu.name().equals(value)) {
                return eu.code();
            }
        }
        return 0;
    }

    /**
     * 根据编码返回提示消息
     *
     * @param code
     * @return
     */
    public static String getMessage(int code) {
        for (Status eu : Status.values()) {
            if (eu.code() == code) {
                return eu.message();
            }
        }
        return null;
    }
}

