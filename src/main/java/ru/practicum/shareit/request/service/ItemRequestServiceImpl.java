package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
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
    private final Sort sortByCreatedDesc = Sort.by("created").descending();

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
    public List<ItemRequestFullDto> getAllOwn(Long requesterId) {
        log.debug("/getAllOwn");
        userService.isExist(requesterId);
        List<ItemRequest> requests = requestStorage.findByRequester_Id(requesterId); // получили реквесты юзера
        Map<Long, List<Item>> requestIdItems = getRequestItems(requests);
        List<ItemRequestFullDto> result = new ArrayList<>();
        requests.forEach(request -> result.add(toItemRequestDtoWithItem(request, requestIdItems.get(request.getId()))));
        return result;
    }

    @Override
    public List<ItemRequestFullDto> getAllNotOwn(Long requesterId) {
        log.debug("/getAllNotOwn");
        userService.isExist(requesterId);
        List<ItemRequest> requests = requestStorage.findByRequester_IdNot(requesterId);
        Map<Long, List<Item>> requestIdItems = getRequestItems(requests);
        List<ItemRequestFullDto> result = new ArrayList<>();
        requests.forEach(request -> result.add(toItemRequestDtoWithItem(request, requestIdItems.get(request.getId()))));
        return result;
    }

    private Map<Long, List<Item>> getRequestItems(List<ItemRequest> requests) {
        log.debug("/getRequestItems");
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemStorage.findByRequest_IdIn(requestIds);
        Map<Long, List<Item>> result = new HashMap<>(); // key = requestId, value = his items
        items.forEach(item -> {
            Long currentItemRequestId = item.getRequest().getId();
            List<Item> currentRequestItems = result.getOrDefault(currentItemRequestId, new ArrayList<>());
            currentRequestItems.add(item);
            result.put(item.getRequest().getId(), currentRequestItems);
        });
        return result;
    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}