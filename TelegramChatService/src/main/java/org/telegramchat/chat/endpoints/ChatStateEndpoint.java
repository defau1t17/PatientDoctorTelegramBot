package org.telegramchat.chat.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegramchat.chat.entity.ChatState;
import org.telegramchat.chat.entity.ChatStates;
import org.telegramchat.chat.service.StateService;

import java.util.Optional;

import static java.util.Arrays.*;

@Tag(name = "Chat State Endpoint", description = "Manage Telegram chat state")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatstate")
public class ChatStateEndpoint {

    private final StateService stateService;

    @Operation(summary = "Get page of chat state", tags = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatState[].class)))
    })
    @GetMapping
    public ResponseEntity<?> findAllChatStates(@RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                               @RequestParam(value = "pageSize") Optional<Integer> pageSize) {
        return ResponseEntity
                .ok(stateService.findAll(pageNumber.orElse(0), pageSize.orElse(5)));
    }

    @Operation(summary = "Get chat state by chat ID", tags = "GET", parameters = @Parameter(name = "chatID", example = "1321"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatState.class))),
            @ApiResponse(responseCode = "404")
    })
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

    @Operation(summary = "Create chat state by chat ID", tags = "POST", parameters = @Parameter(name = "chatID", example = "32423"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatState.class))),
            @ApiResponse(responseCode = "400")
    })
    @PostMapping
    public ResponseEntity<?> createNewChatState(@RequestParam(value = "chatID") long chatID) {
        return stateService.validateBeforeSave(new ChatState(chatID)) ?
                ResponseEntity
                        .ok(stateService.create(new ChatState(chatID))) :
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
    }


    @Operation(summary = "Update chat state ", tags = "POST", parameters = {@Parameter(name = "chatID", example = "32423"), @Parameter(name = "state", example = "DEFAULT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatState.class))),
            @ApiResponse(responseCode = "400")
    })
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

    @Operation(summary = "Move chat state to next state", tags = "POST", parameters = {@Parameter(name = "chatID", example = "32423"), @Parameter(name = "move", example = "NEXT")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatState.class))),
            @ApiResponse(responseCode = "404")
    })
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
