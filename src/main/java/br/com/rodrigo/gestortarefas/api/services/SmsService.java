package br.com.rodrigo.gestortarefas.api.services;

import br.com.rodrigo.gestortarefas.api.exception.MensagensError;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public String enviarSms(String para, String mensagemBody) {
        try {
            Message mensagem = Message.creator(
                    new PhoneNumber(para),
                    new PhoneNumber(twilioPhoneNumber),
                    mensagemBody
            ).create();
            return MensagensError.SMS_ENVIADO_SUCESSO.getMessage(mensagem.getSid());
        } catch (Exception e) {
            return MensagensError.ERRO_ENVIO_SMS.getMessage(e.getMessage());
        }
    }

    public void enviarSenhaSms(String para, String senha) {
        String mensagem = String.format(
                "Olá! Sua conta foi criada com sucesso. Sua senha é: %s. " +
                        "Se você não solicitou esta conta, entre em contato com o suporte imediatamente.",
                senha
        );
        enviarSms(para, mensagem);
    }
}
