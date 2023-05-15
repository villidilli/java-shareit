package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.NotFoundException.REQUEST_NOT_FOUND;
import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestStorage requestStorage;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto requestDto, BindingResult br, Long userId) {
        log.debug("/create");
        userService.isExist(userId);
        annotationValidate(br);
        User requester = userStorage.getReferenceById(userId);
        ItemRequest savedRequest = requestStorage.save(toItemRequest(requestDto, requester));
        return toItemRequestDto(savedRequest);
    }

    @Override
    public void isExist(Long requestId) throws NotFoundException {
        log.debug("/isExist");
        if (!requestStorage.existsById(requestId)) throw new NotFoundException(REQUEST_NOT_FOUND);
    }

    @Override
    public List<ItemRequestFullDto> getByRequester(Long requesterId) {
        log.debug("/getByRequester");
        userService.isExist(requesterId);
        List<ItemRequest> requests = requestStorage.findByRequester_Id(requesterId); // получили реквесты юзера
        log.debug("ПОЛУЧИЛИ РЕКВЕСТЫ ЮЗЕРА " + requests);
        Map<Long, List<Item>> requestIdItems = getRequestItems(requests);
        log.debug("ПОЛУЧИЛИ МАПУ РЕКВЕСТИ ИД ИТЕМЫ " + requestIdItems.values());
        List<ItemRequestFullDto> result = new ArrayList<>();
        requests.forEach(request -> result.add(toItemRequestDtoWithItem(request, requestIdItems.get(request.getId()))));
        log.debug("РЕСАЛТ ПОСЛЕ МАПИНГА " + result);
        return result;
    }

    private Map<Long, List<Item>> getRequestItems(List<ItemRequest> requests) {
        log.debug("/getRequestItems");
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()); // выдернули айди айтемов из реквестов
        List<Item> items = itemStorage.findByRequest_IdIn(requestIds); // получили сами айтемы
        Map<Long, List<Item>> result = new HashMap<>();
        items.forEach(item -> {
            List<Item> requestItems = result.getOrDefault(item.getRequest().getId(), new ArrayList<>());
            requestItems.add(item);
            result.put(item.getRequest().getId(), requestItems);
        });
        return result;
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}