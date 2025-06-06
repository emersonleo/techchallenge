package br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.services;

import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.dtos.ClienteRequestDTO;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.dtos.ClienteResponseDTO;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.dtos.UpdatePasswordDTO;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.entities.Cliente;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.repositories.ClienteRepository;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.services.exceptions.ClienteNotFoundException;
import br.com.fiap.tech.challenge.grupo.vinte.oito.chefonline.services.exceptions.DadoInvalidoException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final SenhaService senhaService;

    public ClienteService(ClienteRepository clienteRepository, SenhaService senhaService) {
        this.clienteRepository = clienteRepository;
        this.senhaService = senhaService;
    }

    public void criaCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = new Cliente(clienteRequestDTO);
        cliente.getUsuario().setSenha(senhaService.hashSenha(cliente.getUsuario().getSenha()));
        cliente.getUsuario().setDataCriacaoRegistro(LocalDateTime.now());
        clienteRepository.save(cliente);

    }

    public List<ClienteResponseDTO> buscaTodosClientes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = clienteRepository.findAll(pageable);
        return pageResult.getContent()
                .stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    public ClienteResponseDTO buscaClientePorId(Long id) {
        var cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o id: " + id));
        return new ClienteResponseDTO(cliente);
    }

    public Optional<Cliente> buscaClientPorLogin(String login) {
        return clienteRepository.findByUsuarioLogin(login);
    }

    public void atualizaCliente(ClienteRequestDTO clienteRequestDTO, Long id) {
        var clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o id: " + id));

        clienteExistente.getUsuario().setNome(clienteRequestDTO.nome());
        clienteExistente.getUsuario().setEmail(clienteRequestDTO.email());
        clienteExistente.getUsuario().setLogin(clienteRequestDTO.login());
        clienteExistente.getUsuario().setSenha(clienteRequestDTO.senha());
        clienteExistente.getUsuario().setEndereco(clienteRequestDTO.endereco());
        clienteExistente.getUsuario().setDataUltimaAlteracaoRegistro(LocalDateTime.now());

        clienteRepository.save(clienteExistente);
    }

    public void deletaCliente(Long id) {
        var clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o id: " + id));

        clienteRepository.delete(clienteExistente);
    }


    public void atualizaSenha(UpdatePasswordDTO updatePasswordDTO) {
        var cliente = clienteRepository.findByUsuarioLogin(updatePasswordDTO.login())
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o login: " + updatePasswordDTO.login()));

        if (!cliente.getCpf().equals(updatePasswordDTO.cpfCnpj())) {
            throw new DadoInvalidoException("CPF não corresponde ao cliente.");
        }

        cliente.getUsuario().setSenha(senhaService.hashSenha(updatePasswordDTO.novaSenha()));
        cliente.getUsuario().setDataUltimaAlteracaoRegistro(LocalDateTime.now());
        clienteRepository.save(cliente);
    }

}
