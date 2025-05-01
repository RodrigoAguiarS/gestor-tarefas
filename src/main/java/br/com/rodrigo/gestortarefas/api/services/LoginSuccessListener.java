package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.model.Usuario;
import br.com.rodrigo.gestortarefas.api.util.IpAddressUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final IRegistroEntrada registroEntradaService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Usuario usuario = (Usuario) event.getAuthentication().getPrincipal();
        String ipAddress = IpAddressUtil.getLocalIpAddress();
        registroEntradaService.registrarEntrada(usuario, ipAddress);
    }
}
