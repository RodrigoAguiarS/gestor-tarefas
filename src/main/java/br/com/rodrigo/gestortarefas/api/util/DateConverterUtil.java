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

    public static LocalDateTime parse(String dateString, boolean inicioDoDia) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                return ajustarHorario(dateTime, inicioDoDia);
            } catch (DateTimeParseException e) {
                try {
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    LocalDateTime dateTime = date.atStartOfDay();
                    return ajustarHorario(dateTime, inicioDoDia);
                } catch (DateTimeParseException ignored) {
                }
            }
        }

        throw new DateTimeParseException("Não foi possível parsear a data: " + dateString, dateString, 0);
    }

    private static LocalDateTime ajustarHorario(LocalDateTime dateTime, boolean inicioDoDia) {
        if (inicioDoDia) {
            return dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else {
            return dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }
    }
}
