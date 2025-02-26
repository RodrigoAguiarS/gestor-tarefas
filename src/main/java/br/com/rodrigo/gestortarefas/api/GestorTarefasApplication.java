package br.com.rodrigo.gestortarefas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GestorTarefasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorTarefasApplication.class, args);
	}

}
