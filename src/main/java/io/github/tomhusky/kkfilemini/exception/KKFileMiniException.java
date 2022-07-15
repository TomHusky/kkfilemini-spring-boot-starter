package io.github.tomhusky.kkfilemini.exception;

/**
 * <p>
 * 基本异常
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/7/13 18:18
 */
public class KKFileMiniException extends RuntimeException {

    /**
     * 异常信息
     */
    private final String message;

    public KKFileMiniException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
