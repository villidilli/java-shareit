package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private static Map<String, Object> parameters;

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestDto requestDto, BindingResult br, long userId) {
        log.debug("[GATEWAY]/create");
        annotationValidate(br);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getAllOwn(long requesterId) {
        log.debug("[GATEWAY]/getAllOwn");
        return get("", requesterId);
    }

    public ResponseEntity<Object> getAllNotOwn(long requesterId, int from, int size) {
        log.debug("[GATEWAY]/getAllNotOwn");
        parameters = Map.of(
          "from", from,
          "size", size
        );
        return get("/all", requesterId, parameters);
    }

    public ResponseEntity<Object> getById(long requesterId, long requestId) {
        log.debug("[GATEWAY]/getById");
        return get("/" + requestId, requesterId);
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("[GATEWAY]/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}
