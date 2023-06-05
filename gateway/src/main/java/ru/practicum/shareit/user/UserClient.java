package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Service
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";
    private static Map<String, Object> parameters;

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(UserDto userDto, BindingResult br) {
        log.debug("[GATEWAY]/create");
        annotationValidate(br);
        return post("", userDto);
    }

    public ResponseEntity<Object> update(long userId, UserDto userDto) {
        log.debug("[GATEWAY]/update");
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> get(long userId) {
        log.debug("[GATEWAY]/get");
        return get("/" + userId);
    }

    public void delete(long userId) {
        log.debug("[GATEWAY]/delete");
        delete("/" + userId);
    }

    public ResponseEntity<Object> getAll() {
        log.debug("[GATEWAY]/getAll");
        return get("");
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("[GATEWAY]/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}