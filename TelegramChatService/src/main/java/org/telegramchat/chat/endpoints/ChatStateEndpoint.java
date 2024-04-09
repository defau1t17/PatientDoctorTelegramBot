package org.telegramchat.chat.endpoints;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegramchat.chat.entity.ChatState;
import org.telegramchat.chat.entity.ChatStates;
import org.telegramchat.chat.service.StateService;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatstate")
public class ChatStateEndpoint {

    private final StateService stateService;

    @GetMapping
    public ResponseEntity<?> findAllChatStates(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                               @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(stateService.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @GetMapping("/{chatID}")
    public ResponseEntity<?> findChatStateByChatID(@PathVariable(value = "chatID") long chatID) {
        Optional<ChatState> chatStateOptional = stateService.findByChatID(chatID);
        if (chatStateOptional.isPresent()) {
            return ResponseEntity
                    .ok(chatStateOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createNewChatState(@RequestParam(value = "chatID") long chatID) {
        return stateService.validateBeforeSave(new ChatState(chatID)) ?
                ResponseEntity
                        .ok(stateService.create(new ChatState(chatID))) :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }


    @PostMapping("/{chatID}/update")
    public ResponseEntity<?> updateChatState(@PathVariable("chatID") long chatID, @RequestParam(value = "state") ChatStates chatStates) {
        Optional<ChatState> optionalChatState = stateService.findByChatID(chatID);
        return optionalChatState.isPresent() && asList(ChatStates.values()).contains(chatStates) ?
                ResponseEntity
                        .ok(stateService.update(optionalChatState.get().updateChatState(chatStates))) :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }

    @PostMapping("/{chatID}/move")
    public ResponseEntity<?> moveChatState(@PathVariable(value = "chatID") long chatID, @RequestParam(value = "move") String move) {
        Optional<ChatState> optionalChatState = stateService.findByChatID(chatID);
        return optionalChatState.isPresent() ?
                ResponseEntity
                        .ok(stateService.moveState(optionalChatState.get(), move)) :
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
    }
}
