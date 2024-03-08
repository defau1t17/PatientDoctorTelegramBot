package org.telegrambots.doctortelegrambot.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegrambots.doctortelegrambot.entities.ChatState;
import org.telegrambots.doctortelegrambot.repositories.ChatStateRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class StateCreatorAspect {

    private final ChatStateRepository chatStateRepository;


    @Around(value = "execution(public * org.telegrambots.doctortelegrambot.services.AuthenticationService.authenticate(..))")
    public Object createStateAfterReturn(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = proceedingJoinPoint.proceed();

        if ((boolean) result && validateIncomingDataArray(proceedingJoinPoint.getArgs())) {
            ChatState chatState = new ChatState(extractChatID(proceedingJoinPoint.getArgs()));
            chatStateRepository.save(chatState);
        }
        return result;
    }

    private boolean validateIncomingDataArray(Object[] array) {
        return array != null && array[0] != null;
    }

    private int extractChatID(Object[] array) {
        return (int) array[0];
    }
}
