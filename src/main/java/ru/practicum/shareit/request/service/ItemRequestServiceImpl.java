package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toItemRequest;
import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toItemRequestDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final UserStorage userStorage;
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

//    @Override
//    public ItemRequestDto getByRequester(Long requesterId) {
//        log.debug("/getByRequester");
//        userService.isExist(requesterId);
//        User requester = userStorage.getReferenceById(requesterId);
//        List<ItemRequest> requests = requestStorage.findByRequester_Id(requesterId);
//    }

    private void annotationValidate(BindingResult br) throws ValidateException {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }
}