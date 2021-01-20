package com.legend.exception;

/**
 * @author Legend
 * @data by on 20-9-11.
 * @description 代码生成器异常
 */
public class GeneratorException extends Throwable {

    private String msg;

    public GeneratorException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
