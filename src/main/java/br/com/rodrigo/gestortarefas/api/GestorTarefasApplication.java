package br.com.rodrigo.gestortarefas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
public class GestorTarefasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorTarefasApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
		System.out.println("Timezone atual: " + TimeZone.getDefault().getID());
	}
}
