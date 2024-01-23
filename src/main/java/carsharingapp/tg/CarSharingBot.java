package carsharingapp.tg;

import carsharingapp.exception.TelegramBotMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class CarSharingBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.chatId}")
    private String chatId;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        chatId = String.valueOf(update.getMessage().getChatId());
        System.out.println("Your chatId: " + chatId);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            if (message.equals("/start")) {
                startingCommand(update.getMessage().getChat().getFirstName());
            }
        }
    }

    private void startingCommand(String name) {
        String message = """
                Hi, %s, nice to meet you!
                                               \s
                In this bot you will get notifications on
                - created rentals
                - successful payments
                - notifications if you have any overdue rentals
                                               \s
                We hope you will enjoy your trip!
                """.formatted(name);
        sendMessage(message);
    }

    public void sendMessage(String value) {
        SendMessage message = new SendMessage(chatId, value);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotMessageException("Couldn't send a message" + value, e);
        }
    }
}
