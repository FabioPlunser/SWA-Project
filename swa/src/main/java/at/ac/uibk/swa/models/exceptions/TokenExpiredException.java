package at.ac.uibk.swa.models.exceptions;

import org.springframework.security.access.AccessDeniedException;


public class TokenExpiredException extends AccessDeniedException {

    public TokenExpiredException(String msg) {
        super(msg);
    }

    public TokenExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
