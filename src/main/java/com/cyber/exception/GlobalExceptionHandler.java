package com.cyber.exception;

import com.cyber.constant.Constant;
import com.cyber.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.validation.UnexpectedTypeException;
import java.sql.SQLException;

public class GlobalExceptionHandler {

    private static final Logger LOGGING = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public Response missingServletRequestParameterException(MissingServletRequestParameterException exception) {
        LOGGING.error("missing servlet request parameter exception {} ...", exception);
        return Response.fail(Constant.ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Response argumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGING.error("method argument not valid exception {} ...", exception);
        return Response.fail(Constant.ResultCode.VALIDATE_ERROR.getStatusCode(), exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = {BindException.class})
    public Response bindException(BindException exception) {
        LOGGING.error("bind exception {} ...", exception);
        return Response.fail(Constant.ResultCode.VALIDATE_ERROR.getStatusCode(), exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = {UnexpectedTypeException.class})
    public Response unexpectedTypeException(UnexpectedTypeException exception) {
        LOGGING.error("unexpected type exception {} ...", exception);
        return Response.fail(Constant.ResultCode.VALIDATE_ERROR);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public Response methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        LOGGING.error("http request method not supported exception {} ...", exception);
        return Response.fail(Constant.ResultCode.REST_METHOD_NOT_SUPPORT);
    }

    @ExceptionHandler(value = {MultipartException.class})
    public Response uploadFileLimitException(MultipartException exception) {
        LOGGING.error("upload file limit exception {} ...", exception);
        return Response.fail(Constant.ResultCode.FILE_UPLOAD_ERROR);
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public Response duplicateKeyException(DuplicateKeyException exception) {
        LOGGING.error("duplicate key exception {} ...", exception);
        return Response.fail(Constant.ResultCode.BAD_SQL_ERROR);
    }

    @ExceptionHandler(value = {SQLException.class})
    public Response sqlException(DuplicateKeyException exception) {
        LOGGING.error("sql exception {} ... ", exception);
        return Response.fail(Constant.ResultCode.BAD_SQL_ERROR);
    }

    @ExceptionHandler(value = {BusinessException.class})
    public Response businessException(BusinessException exception) {
        LOGGING.error("business exception {} ... ", exception);
        return Response.fail(Constant.ResultCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = {SystemException.class})
    public Response systemException(SystemException exception) {
        LOGGING.error("system exception {} ... ", exception);
        return Response.fail(Constant.ResultCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception exception) {
        LOGGING.error("exception {} ... ", exception);
        return Response.fail(Constant.ResultCode.SERVER_ERROR);
    }

    @ExceptionHandler(value = {Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response throwable(Throwable throwable) {
        LOGGING.error("throwable {} ... ", throwable);
        return Response.fail(Constant.ResultCode.SERVER_ERROR);
    }
}