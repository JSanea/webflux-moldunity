package web.app.webflux_moldunity.exception;

import org.springframework.transaction.TransactionException;

public class SaveFailedException extends TransactionException {
    private static final long serialVersionUID = 1L;

    public SaveFailedException(String msg) {
        super(msg);
    }

    public SaveFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
