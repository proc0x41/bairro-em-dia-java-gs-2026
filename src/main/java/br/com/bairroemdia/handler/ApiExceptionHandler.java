package br.com.bairroemdia.handler;
import br.com.bairroemdia.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import java.time.Instant;
import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
    public record Erro(Instant timestamp,int status,String mensagem,String caminho,Map<String,String> campos) {}
    private ResponseEntity<Erro> erro(int status,String msg,HttpServletRequest req,Map<String,String> campos) { return ResponseEntity.status(status).body(new Erro(Instant.now(),status,msg,req.getRequestURI(),campos)); }
    @ExceptionHandler(NaoEncontradoException.class) public ResponseEntity<Erro> ausente(NaoEncontradoException e,HttpServletRequest r) { return erro(404,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(RegraNegocioException.class) public ResponseEntity<Erro> regra(RegraNegocioException e,HttpServletRequest r) { return erro(409,e.getMessage(),r,Map.of()); }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<Erro> integridade(Exception e,HttpServletRequest r) { return erro(409,"Operação conflita com registros existentes. Verifique duplicidade e vínculos antes de remover.",r,Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Erro> validacao(MethodArgumentNotValidException e,HttpServletRequest r) {
        Map<String,String> campos=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(f->campos.putIfAbsent(f.getField(),f.getDefaultMessage()));
        return erro(400,"Confira os campos informados.",r,campos);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,ConstraintViolationException.class,HandlerMethodValidationException.class}) public ResponseEntity<Erro> entrada(Exception e,HttpServletRequest r) { return erro(400,"Dados inválidos. Confira tipos, formatos e campos obrigatórios.",r,Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) public ResponseEntity<Erro> metodo(HttpRequestMethodNotSupportedException e,HttpServletRequest r) {
        var response=erro(405,"Método HTTP não permitido para esta rota.",r,Map.of());
        return ResponseEntity.status(405).allow(e.getSupportedHttpMethods().toArray(HttpMethod[]::new)).body(response.getBody());
    }
    @ExceptionHandler(NoResourceFoundException.class) public ResponseEntity<Erro> rota(Exception e,HttpServletRequest r) { return erro(404,"Rota não encontrada.",r,Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) public ResponseEntity<Erro> formato(Exception e,HttpServletRequest r) { return erro(415,"Envie o corpo com Content-Type application/json.",r,Map.of()); }
    @ExceptionHandler(Exception.class) public ResponseEntity<Erro> inesperado(Exception e,HttpServletRequest r) { return erro(500,"Não foi possível concluir a operação. Tente novamente mais tarde.",r,Map.of()); }
}
