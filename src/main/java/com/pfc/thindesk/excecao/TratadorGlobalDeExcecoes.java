package com.pfc.thindesk.excecao;

import com.pfc.thindesk.dto.RespostaErroDTO;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class TratadorGlobalDeExcecoes {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaErroDTO> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        return responder(HttpStatus.NOT_FOUND, excecao.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<RespostaErroDTO> tratarRegraDeNegocio(RegraDeNegocioException excecao) {
        return responder(HttpStatus.BAD_REQUEST, excecao.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErroDTO> tratarValidacao(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .orElse("Dados inválidos.");
        return responder(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespostaErroDTO> tratarAcessoNegado(AccessDeniedException excecao) {
        return responder(HttpStatus.FORBIDDEN, "Acesso negado a este recurso.");
    }

    private ResponseEntity<RespostaErroDTO> responder(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new RespostaErroDTO(status.value(), mensagem, LocalDateTime.now()));
    }
}
