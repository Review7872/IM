package online.fadai.msghandler.controllerAdvice;

import jakarta.annotation.Resource;
import online.fadai.pojo.Status;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdviceConfig {
    @Resource(name = "exceptionStatus")
    private Status exceptionStatus;
    @ExceptionHandler(Exception.class)
    public Status nullPointException(){
        return exceptionStatus;
    }
}
