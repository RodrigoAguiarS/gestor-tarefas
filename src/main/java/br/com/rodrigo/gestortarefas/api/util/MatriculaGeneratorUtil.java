package br.com.rodrigo.gestortarefas.api.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class MatriculaGeneratorUtil {

    private static final AtomicInteger SEQUENCIAL = new AtomicInteger(1);

    public static String gerarMatricula() {

        String prefixo = "FUNC";

        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String numeroSequencial = String.format("%04d", SEQUENCIAL.getAndIncrement());

        return String.format("%s-%s-%s", prefixo, dataAtual, numeroSequencial);
    }
}
