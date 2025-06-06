package br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.controllers;

import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.dtos.UsuarioLoginDTO;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.services.SessaoClienteService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sessao/clientes")
public class SessaoClienteController {
    private static final Logger logger = LoggerFactory.getLogger(SessaoClienteController.class);

    private final SessaoClienteService sessaoClienteService;

    SessaoClienteController(SessaoClienteService sessaoClienteService) {
        this.sessaoClienteService = sessaoClienteService;
    }

    @PostMapping({"/login"})
    public ResponseEntity<?> login(
            @Valid @RequestBody UsuarioLoginDTO usuarioLoginDTO
            ) {
        logger.info("POST -> /v1/sessao/clientes/login");

        sessaoClienteService.login(usuarioLoginDTO.login(), usuarioLoginDTO.senha());

        return ResponseEntity.ok("usuario logado");
    }
}
