package net.s3managerApi.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final String NOT_FOUND_REASON = "The required object was not found.";
    private static final String INTEGRITY_VIOLATION = "Integrity constraint has been violated.";
    private static final String BAD_REQUEST_REASON = "Incorrectly made request.";
    private static final String UNAUTHORIZED_REASON = "User authorization failed";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApi handleException(Exception exception) {
        log.error("ERROR: ", exception);
        return new ErrorApi(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), exception.getClass().toString());
    }

    @ExceptionHandler(value = {
        EntityNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApi handeEntityNotFoundException(Exception e) {
        return new ErrorApi(HttpStatus.NOT_FOUND, e.getMessage(), NOT_FOUND_REASON);
    }

    @ExceptionHandler(value = {
        DataIntegrityViolationException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorApi handleDataIntegrityViolationException(Exception e) {
        return new ErrorApi(HttpStatus.CONFLICT, e.getMessage(), INTEGRITY_VIOLATION);
    }

    @ExceptionHandler(value = {
        MissingServletRequestParameterException.class,
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class,
        AmazonS3Exception.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi handleMissingServletRequestParameterException(Exception e) {
        return new ErrorApi(HttpStatus.BAD_REQUEST, e.getMessage(), BAD_REQUEST_REASON);
    }

    @ExceptionHandler(value = {
        AuthenticationException.class,
        JwtException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorApi handleAuthException(Exception e) {
        return new ErrorApi(HttpStatus.UNAUTHORIZED, e.getMessage(), UNAUTHORIZED_REASON);
    }
}
