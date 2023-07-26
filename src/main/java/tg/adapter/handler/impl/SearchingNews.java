package tg.adapter.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import tg.adapter.handler.Handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * Class for processing /search request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchingNews implements Handler {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName = "TELEGRAM_ADAPTER";

    @Override
    public String handle(Update update) {
        try {
            log.info("Start work /search method ");
            String message = update.getMessage().getText();
            String[] array = Arrays.stream(message.trim().split(",")).map(el -> el.trim()).toArray(String[]::new);
            if (array.length == 1 && array[0].trim().equals("/search")) {
                return "Options of this function is searching news.\n" +
                        "Enter keywords for the news you want to watch and " +
                        "at the end write for what date you want to see this news\n" +
                        "For example,  /search football,weather report,25.10.2020";
            }
            StringBuilder sb = new StringBuilder(array[0]);
            String remove = "/search ";
            sb.delete(sb.indexOf(remove), sb.indexOf(remove) + remove.length());
            array[0] = sb.toString().trim();
            String dateFormat = "dd.MM.yyyy";
            if (dateIsValid(array[array.length - 1].trim(), dateFormat)) {
                log.info("#### Producing message");
                kafkaTemplate.send(topicName, writeValueAsString(array));
                log.info("Sent message to Kafka  -> " + writeValueAsString(array));
                return Arrays.toString(array); //TODO: return news
            } else {
                return "Date not valid. Enter in correct format. For example: 25.10.2020";
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.error("Validation error /search method: {}", e.getMessage(), e);
            return "Validation error, enter in the correct format\n" +
                    "For example,  /search football,weather report,25.10.2020";
        }
    }

    private String writeValueAsString(String[] array) {
        try {
            return objectMapper.writeValueAsString(array);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + Arrays.toString(array));
        }
    }

    private boolean dateIsValid(String string, String dateFormat) throws DateTimeParseException {
        try {
            LocalDate.parse(string, DateTimeFormatter.ofPattern(dateFormat));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
