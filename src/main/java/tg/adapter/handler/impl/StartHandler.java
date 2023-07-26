package tg.adapter.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import tg.adapter.handler.Handler;

/**
 * Class for processing /start request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartHandler implements Handler {
    @Override
    public String handle(Update update) {
        log.info("Start working startHandler");
        Long userId = update.getMessage().getFrom().getId(); //будем сохранять юзеров в бд?
        String userName = update.getMessage().getFrom().getFirstName();
        return String.format("Hi, %s. I'm a TelegramBot who can help you find news", userName);
    }
}
