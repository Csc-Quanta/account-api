package org.csc.account.exception;

/**
 * 分片异常处理
 * @author lance
 * @since 2019.1.14 10:32
 */
public class SliceException extends RuntimeException{
    public SliceException() {
        super();
    }

    public SliceException(String message) {
        super(message);
    }
}
