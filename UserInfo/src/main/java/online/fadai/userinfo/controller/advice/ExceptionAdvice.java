package online.fadai.userinfo.controller.advice;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import online.fadai.pojo.Status;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @Resource(name = "exceptionStatus")
    private Status exceptionStatus;
    @ExceptionHandler(Exception.class)
    public Status exception(Exception e){
        log.error(e.getMessage());
        return exceptionStatus;
    }
}
