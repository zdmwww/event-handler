package site.autzone.event.handler.rest.exception;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import site.autzone.event.handler.rest.domain.ItemNotFoundException;

/**
 * 根据异常组织异常信息返回
 *
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), getStackTrace(ex),
        request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(SQLException.class)
  public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(SQLException ex, WebRequest request) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), getStackTrace(ex),
        request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }
  
  //参数错误校验
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Validation Failed",
        ex.getBindingResult().toString());
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ItemNotFoundException.class)
  public final ResponseEntity<ExceptionResponse> itemNotFoundException(Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), getStackTrace(ex),
        request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
  }
  
  protected String getStackTrace(Throwable aThrowable) {
	final Writer result = new StringWriter();
	final PrintWriter printWriter = new PrintWriter(result);
	aThrowable.printStackTrace(printWriter);
	return result.toString();
  }
}