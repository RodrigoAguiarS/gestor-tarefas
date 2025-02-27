package br.com.rodrigo.gestortarefas.api.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AutenticacaoCache {

    private final Map<String, Authentication> cache = new HashMap<>();

    public void salvar(String email, Authentication autenticacao) {
        cache.put(email, autenticacao);
    }

    public Authentication recuperar(String email) {
        return cache.get(email);
    }

    public void remover(String email) {
        cache.remove(email);
    }
}
