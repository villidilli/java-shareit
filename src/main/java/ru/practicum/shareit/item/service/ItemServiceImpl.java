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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.ITEM_NOT_HAVE_BOOKING_BY_USER;
import static ru.practicum.shareit.item.dto.CommentDtoMapper.toComment;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;


    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId, BindingResult br) {
        log.debug("/create");
        annotationValidate(br);
        userService.isExist(ownerId);
        Item item = toItem(itemDto, ownerId);
        return toItemDto(itemStorage.save(item));
    }

    @SneakyThrows
    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        log.debug("/update");
        isExist(itemId);
        userService.isExist(ownerId);
        isOwnerOfItem(itemId, ownerId);
        Item existedItem = itemStorage.findById(itemId).get();
        Item itemWithUpdate = toItem(itemDto, ownerId);
        Item updatedItem = setNewFields(existedItem, itemWithUpdate);
        return toItemDto(itemStorage.save(updatedItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoWithBooking get(Long itemId, Long ownerId) {
        log.debug("/get");
        isExist(itemId);
        Item foundedItem = itemStorage.findById(itemId).get();
        ItemDtoWithBooking itemDto;
        try {
            isOwnerOfItem(itemId, ownerId);
            itemDto = toItemDtoWithBooking(foundedItem, getLastBooking(itemId), getNextBooking(itemId));
        } catch (NotFoundException e) {
            itemDto = toItemDtoWithBooking(foundedItem, null, null);
        }
        itemDto.setComments(getCommentsDto(itemId));
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoWithBooking> getByOwner(Long ownerId) {
        log.debug("/getByOwner");
        userService.isExist(ownerId);
        List<ItemDtoWithBooking> itemDtos = itemStorage.findByOwnerId(ownerId).stream()
                .map(item -> toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId())))
                .collect(Collectors.toList());
        itemDtos.forEach(itemDto -> itemDto.setComments(getCommentsDto(itemDto.getId())));
        return itemDtos;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsDto(Long itemId) {
        return commentStorage.findAllByItem_Id(itemId).stream()
                .map(CommentDtoMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        log.debug("/search");
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text)
                .stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking getLastBooking(Long itemId) {
        final LocalDateTime curTime = LocalDateTime.now();
        return bookingStorage.findTopByItem_IdAndStatusIsNotAndStartIsBeforeOrderByEndDesc(itemId, REJECTED, curTime);
    }

    @Transactional(readOnly = true)
    public Booking getNextBooking(Long itemId) {
        final LocalDateTime curTime = LocalDateTime.now();
        return bookingStorage.findTopByItem_IdAndStatusIsNotAndStartIsAfterOrderByStartAsc(itemId, REJECTED, curTime);
    }

    @Override
    @Transactional(readOnly = true)
    public void isExist(Long itemId) {
        log.debug("/isExist");
        if(!itemStorage.existsById(itemId)) throw new NotFoundException(ITEM_NOT_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkAvailable(Long itemId) throws ValidateException {
        log.debug("/checkAvailable");
        if(itemStorage.findByIdAndAvailableIsTrue(itemId) == null) throw new ValidateException(ITEM_NOT_FOUND);
    }

    private Item setNewFields(Item existedItem, Item itemWithUpdate) {
        log.debug("/setNewFields");
        Map<String, String> fieldsToUpdate = getFieldToUpdate(itemWithUpdate);
        Map<String, String> existedItemMap = objectMapper.convertValue(existedItem, Map.class);
        existedItemMap.putAll(fieldsToUpdate);
        return objectMapper.convertValue(existedItemMap, Item.class);
    }

    @Override
    public void isOwnerOfItem(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/isOwnerOfItem");
        Long savedItemOwnerId = itemStorage.findById(itemId).get().getOwner().getId();
        if (!Objects.equals(savedItemOwnerId, ownerId)) throw new NotFoundException(OWNER_NOT_MATCH_ITEM);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long bookerId, BindingResult br) {
        log.debug("/createComment");
        log.debug("Пришло DTO " + commentDto);
        log.debug("Пришло itemId " + itemId);
        log.debug("Пришло bookerId " + bookerId);
        annotationValidate(br);
        isExist(itemId);
        userService.isExist(bookerId);
        isItemBookedByUser(itemId, bookerId);
        Comment savedComment = commentStorage.save(toComment(commentDto, itemId, bookerId));
        log.debug("saved COMMENT " + savedComment);
        return CommentDtoMapper.toCommentDto(savedComment);
    }

    private void isItemBookedByUser(Long itemId, Long bookerId) {
        log.debug("/isItemBookedByUser");
        Long numCompletedBookingsByUser =
                bookingStorage.countBookingsByBooker_IdAndItem_IdAndEndBefore(bookerId, itemId, LocalDateTime.now());
        log.debug("Все было букингов " + numCompletedBookingsByUser);
        if(numCompletedBookingsByUser == 0) throw new ValidateException(ITEM_NOT_HAVE_BOOKING_BY_USER);
    }

    private void annotationValidate(BindingResult br) {
        log.debug("/annotationValidate");
        log.debug(br.getAllErrors().toString() + "ПРИШЛИ ОШИБКИ");
        if (br.hasErrors()) throw new ValidateException(GlobalExceptionHandler.bindingResultToString(br));
    }

    private Map<String, String> getFieldToUpdate(Item itemWithUpdate) {
        log.debug("/getFieldsToUpdate");
        Map<String, String> mapWithNullFields = objectMapper.convertValue(itemWithUpdate, Map.class);
        return mapWithNullFields.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}