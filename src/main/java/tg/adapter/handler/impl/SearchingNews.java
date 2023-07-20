package tg.adapter.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import tg.adapter.handler.Handler;

/**
 * Class for processing /search request
 */
@Slf4j
@Component
public class SearchingNews implements Handler {
    @Override
    public String handle(Update update) {
        log.info("Send correctly /search method response");
        return "now just null";
    }
}
