package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;

import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.ITEM_NOT_HAVE_BOOKING_BY_USER;

import static ru.practicum.shareit.item.dto.CommentDtoMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentDtoMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final UserStorage userStorage;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId, BindingResult br) throws ValidateException, NotFoundException {
        log.debug("/create");
        annotationValidate(br);
        userService.isExist(ownerId);
        User owner = userStorage.getReferenceById(ownerId);
        return toItemDto(itemStorage.save(toItem(itemDto, owner)));
    }

    @Transactional
    @SneakyThrows
    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) throws NotFoundException {
        log.debug("/update");
        isExist(itemId);
        userService.isExist(ownerId);
        isOwnerOfItem(itemId, ownerId);
        User owner = userStorage.getReferenceById(ownerId);
        Item existedItem = itemStorage.findById(itemId).get();
        Item itemWithUpdate = toItem(itemDto, owner);
        Item updatedItem = setNewFields(existedItem, itemWithUpdate);
        return toItemDto(itemStorage.save(updatedItem));
    }

    @Override
    public ItemDtoWithBooking get(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/get");
        isExist(itemId);
        userService.isExist(ownerId);
        Item foundedItem = itemStorage.findById(itemId).get();
        ItemDtoWithBooking itemDto;
        try {
            isOwnerOfItem(itemId, ownerId);
            itemDto = toItemDtoWithBooking(foundedItem, getLastBooking(itemId), getNextBooking(itemId));
        } catch (NotFoundException e) {
            itemDto = toItemDtoWithBooking(foundedItem, null, null);
        }
        itemDto.setComments(getCommentDtos(itemId));
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getByOwner(Long ownerId) throws NotFoundException {
        log.debug("/getByOwner");
        userService.isExist(ownerId);
        List<ItemDtoWithBooking> itemDtos = itemStorage.findByOwnerId(ownerId).stream()
                .map(item -> toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId())))
                .collect(Collectors.toList());
        itemDtos.forEach(itemDto -> itemDto.setComments(getCommentDtos(itemDto.getId())));
        return itemDtos;
    }

    @Override
    public List<ItemDto> search(String text) {
        log.debug("/search");
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long bookerId, BindingResult br)
                                                            throws ValidateException, NotFoundException {
        log.debug("/createComment");
        annotationValidate(br);
        isExist(itemId);
        userService.isExist(bookerId);
        isUserBookedItem(itemId, bookerId);
        User author = userStorage.getReferenceById(bookerId);
        Item item = itemStorage.getReferenceById(itemId);
        return toCommentDto(commentStorage.save(toComment(commentDto, item, author)));
    }

    @Override
    public void isExist(Long itemId) throws NotFoundException {
        log.debug("/isExist");
        if(!itemStorage.existsById(itemId)) throw new NotFoundException(ITEM_NOT_FOUND);
    }

    @Override
    public void isItemAvailable(Long itemId) throws ValidateException {
        log.debug("/checkAvailable");
        if(itemStorage.findByIdAndAvailableIsTrue(itemId) == null) throw new ValidateException(ITEM_NOT_FOUND);
    }

    @Override
    public void isOwnerOfItem(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/isOwnerOfItem");
        Long savedItemOwnerId = itemStorage.findById(itemId).get().getOwner().getId();
        if (!Objects.equals(savedItemOwnerId, ownerId)) throw new NotFoundException(OWNER_NOT_MATCH_ITEM);
    }

    private Booking getLastBooking(Long itemId) {
        log.debug("/getLastBooking");
        final LocalDateTime curTime = LocalDateTime.now();
        return bookingStorage.findTopByItem_IdAndStatusIsNotAndStartIsBeforeOrderByEndDesc(itemId, REJECTED, curTime);
    }

    private Booking getNextBooking(Long itemId) {
        log.debug("/getNextBooking");
        final LocalDateTime curTime = LocalDateTime.now();
        return bookingStorage.findTopByItem_IdAndStatusIsNotAndStartIsAfterOrderByStartAsc(itemId, REJECTED, curTime);
    }

    private Item setNewFields(Item existedItem, Item itemWithUpdate) {
        log.debug("/setNewFields");
        Map<String, String> fieldsToUpdate = getFieldToUpdate(itemWithUpdate);
        Map<String, String> existedItemMap = objectMapper.convertValue(existedItem, Map.class);
        existedItemMap.putAll(fieldsToUpdate);
        return objectMapper.convertValue(existedItemMap, Item.class);
    }

    private void isUserBookedItem(Long itemId, Long bookerId) {
        log.debug("/isUserHasBookingForItem");
        Long numCompletedBookingsByUser =
                bookingStorage.countBookingsByBooker_IdAndItem_IdAndEndBefore(bookerId, itemId, LocalDateTime.now());
        if(numCompletedBookingsByUser == 0) throw new ValidateException(ITEM_NOT_HAVE_BOOKING_BY_USER);
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private Map<String, String> getFieldToUpdate(Item itemWithUpdate) {
        log.debug("/getFieldsToUpdate");
        Map<String, String> mapWithNullFields = objectMapper.convertValue(itemWithUpdate, Map.class);
        return mapWithNullFields.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<CommentDto> getCommentDtos(Long itemId) {
        log.debug("/getCommentDtos");
        return commentStorage.findAllByItem_Id(itemId).stream()
                .map(CommentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}