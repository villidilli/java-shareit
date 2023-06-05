package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    private static Map<String, Object> parameters;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, BindingResult br, long userId) {
        log.debug("[GATEWAY]/create");
        annotationValidate(br);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, long itemId, long bookerId, BindingResult br) {
        log.debug("[GATEWAY]/create comment");
        annotationValidate(br);
        return post("/" + itemId + "/comment", bookerId, commentDto);
    }

    public ResponseEntity<Object> update(long itemId, ItemDto itemDto, long ownerId) {
        log.debug("[GATEWAY]/update");
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> get(long itemId, long ownerId) {
        log.debug("[GATEWAY]/get");
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getByOwner(long ownerId, int from, int size) {
        log.debug("[GATEWAY]/getByOwner");
        parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> search(long userId, String text, int from, int size) {
        log.debug("[GATEWAY]/search");
        parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}