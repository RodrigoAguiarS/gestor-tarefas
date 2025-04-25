package br.com.rodrigo.gestortarefas.api.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateConverterUtil {
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    public static LocalDateTime parse(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // Tenta parsear como LocalDateTime primeiro
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                try {

                    LocalDate date = LocalDate.parse(dateString, formatter);
                    return date.atStartOfDay();
                } catch (DateTimeParseException ignored) {
                }
            }
        }

        throw new DateTimeParseException("Não foi possível parsear a data: " + dateString, dateString, 0);
    }
}
