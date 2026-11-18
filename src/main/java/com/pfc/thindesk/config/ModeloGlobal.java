package com.pfc.thindesk.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModeloGlobal {

    @ModelAttribute("rota")
    public String rota(HttpServletRequest requisicao) {
        return requisicao.getRequestURI();
    }
}
