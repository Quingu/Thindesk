package com.pfc.thindesk.servico;

import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.repositorio.RepositorioUsuario;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicoAutenticacao implements UserDetailsService {

    private final RepositorioUsuario repositorioUsuario;

    public ServicoAutenticacao(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        String autoridade = "ROLE_" + usuario.getPerfil().name();

        return new User(usuario.getEmail(), usuario.getSenha(), usuario.isAtivo(),
                true, true, true, List.of(new SimpleGrantedAuthority(autoridade)));
    }
}
