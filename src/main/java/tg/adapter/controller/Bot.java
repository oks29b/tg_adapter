package tg.adapter.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tg.adapter.config.TgBotConfig;
import tg.adapter.handler.Handler;

import java.util.Map;

import static tg.adapter.component.BotCommands.LIST_OF_COMMANDS;

/**
 * Controller for endpoints
 */
@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {
    private final TgBotConfig config;
    private final ApplicationContext context;

    public Bot(TgBotConfig config, ApplicationContext context) {
        this.context = context;
        this.config = config;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Value("#{${handlers}}")
    private Map<String, String> handlers;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String[] splitMessageText = messageText.split(" ");
            long chatId = update.getMessage().getChatId();
            String message = handlers.get(splitMessageText[0]);
            if (message == null) {
                sendResponse(chatId, "Incorrect command, please enter /help to see list of commands");
                return;
            }
            Handler handler = (Handler) context.getBean(message);
            sendResponse(chatId, handler.handle(update));
        }
    }

    private void sendResponse(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
            log.info("The response has been sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
