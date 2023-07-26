package tg.adapter.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import tg.adapter.handler.Handler;

/**
 * Class for processing /help request
 */
@Slf4j
@Component
public class HelpHandler implements Handler {

    private static String HELP_TEXT = "Common commands:\n" +
            "/start - start\n" +
            "/search - start searching news\n" +
            "/help - help";

    @Override
    public String handle(Update update) {
        log.info("Send correctly /help method response");
        return HELP_TEXT;
    }
}
