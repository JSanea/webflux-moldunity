package web.app.webflux_moldunity.exception;

public class UserServiceException extends RuntimeException{
    public UserServiceException(String msg){
        super(msg);
    }
}
