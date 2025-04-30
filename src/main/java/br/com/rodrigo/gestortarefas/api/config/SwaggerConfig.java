package br.com.rodrigo.gestortarefas.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API do Sistema Integrado de E-commerce e PDV")
                        .version("1.0")
                        .description("Esta API oferece suporte completo para operações de um sistema de vendas, incluindo loja online (e-commerce) e ponto de venda (PDV). Permite o gerenciamento de produtos, clientes, pedidos, pagamentos, estoque e emissão de relatórios."));
    }
}
