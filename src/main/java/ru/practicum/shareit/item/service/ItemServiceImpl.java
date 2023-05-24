package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.exception.NotFoundException.*;
import static ru.practicum.shareit.exception.ValidateException.ITEM_NOT_HAVE_BOOKING_BY_USER;

import static ru.practicum.shareit.item.dto.CommentDtoMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentDtoMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;
import static ru.practicum.shareit.request.controller.ItemRequestController.DEFAULT_FIRST_PAGE;

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
    private final ItemRequestService requestService;
    private final ItemRequestStorage requestStorage;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, BindingResult br, Long ownerId) throws ValidateException, NotFoundException {
        log.debug("/create");
        annotationValidate(br);
        userService.isExist(ownerId);
        ItemRequest request = getRequest(itemDto.getRequestId());
        User owner = userStorage.getReferenceById(ownerId);
        return toItemDto(itemStorage.save(toItem(itemDto, owner, request)));
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
        ItemRequest request = getRequest(itemDto.getRequestId());
        Item existedItem = itemStorage.findById(itemId).get();
        Item itemWithUpdate = toItem(itemDto, owner, request);
        Item updatedItem = setNewFields(existedItem, itemWithUpdate);
        return toItemDto(itemStorage.save(updatedItem));
    }

    @Override
    public ItemDtoWithBooking get(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/get");
        isExist(itemId);
        userService.isExist(ownerId);
        Item foundedItem = itemStorage.findById(itemId).get();
        List<Booking> bookings = bookingStorage.findByItem_Owner_IdAndItem_Id(ownerId, itemId);
        LocalDateTime currentTime = LocalDateTime.now();
        Map<Long, Booking> lastBookings = getLastBookings(bookings, currentTime);
        Map<Long, Booking> nextBookings = getNextBookings(bookings, currentTime);
        ItemDtoWithBooking itemDto;
        try {
            isOwnerOfItem(itemId, ownerId);
            itemDto = toItemDtoWithBooking(foundedItem, lastBookings.get(itemId), nextBookings.get(itemId));
        } catch (NotFoundException e) {
            itemDto = toItemDtoWithBooking(foundedItem, null, null);
        }
        List<CommentDto> foundedItemCommentDtos =
                getComments(List.of(foundedItem)).getOrDefault(foundedItem.getId(), new ArrayList<>());
        itemDto.setComments(foundedItemCommentDtos);
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getByOwner(Long ownerId, Integer from, Integer size) throws NotFoundException {
        log.debug("/getByOwner");
        userService.isExist(ownerId);
        Page<Item> items = itemStorage.findByOwnerId(ownerId, getPage(from, size, Sort.unsorted()));
        List<Booking> bookings = bookingStorage.findByItem_Owner_Id(ownerId);
        LocalDateTime currentTime = LocalDateTime.now();
        Map<Long, Booking> lastBookings = getLastBookings(bookings, currentTime);
        Map<Long, Booking> nextBookings = getNextBookings(bookings, currentTime);
        Map<Long, List<CommentDto>> commentDtos = getComments(items.toList());
        List<ItemDtoWithBooking> result = new ArrayList<>();
        items.forEach(item -> {
            ItemDtoWithBooking itemDto = toItemDtoWithBooking(item,
                                                              lastBookings.get(item.getId()),
                                                              nextBookings.get(item.getId()));
            itemDto.setComments(commentDtos.getOrDefault(item.getId(), new ArrayList<>()));
            result.add(itemDto);
        });
        return result;
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        log.debug("/search");
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.findByNameContainsIgnoreCaseOrDescriptionContainingIgnoreCase(
                                                                    text, text, getPage(from, size, Sort.unsorted()))
                .stream()
                .filter(Item::getAvailable)
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
//        return null;
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
        if (!itemStorage.existsById(itemId)) throw new NotFoundException(ITEM_NOT_FOUND);
    }

    @Override
    public void isItemAvailable(Long itemId) throws ValidateException {
        log.debug("/checkAvailable");
        if (itemStorage.findByIdAndAvailableIsTrue(itemId) == null) throw new ValidateException(ITEM_NOT_FOUND);
    }

    public void isOwnerOfItem(Long itemId, Long ownerId) throws NotFoundException {
        log.debug("/isOwnerOfItem");
        Long savedItemOwnerId = itemStorage.findById(itemId).get().getOwner().getId();
        if (!Objects.equals(savedItemOwnerId, ownerId)) throw new NotFoundException(OWNER_NOT_MATCH_ITEM);
    }

    @Nullable
    private ItemRequest getRequest(Long requestId) {
        log.debug("/getRequest");
        if (requestId == null) return null;
        requestService.isExist(requestId);
        ItemRequest request = requestStorage.getReferenceById(requestId);
        return request;
    }

    private Pageable getPage(Integer from, Integer size, Sort sort) {
        int firstPage = from != 0 ? from / size : Integer.parseInt(DEFAULT_FIRST_PAGE);
        return PageRequest.of(firstPage, size, sort);
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
        if (numCompletedBookingsByUser == 0) throw new ValidateException(ITEM_NOT_HAVE_BOOKING_BY_USER);
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

    private Map<Long, List<CommentDto>> getComments(List<Item> items) {
        log.debug("/getComments");
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> allComments = commentStorage.findByItem_IdIn(itemIds);
        Map<Long, List<CommentDto>> result = new HashMap<>();
        allComments.forEach(comment -> {
            Long itemId = comment.getItem().getId();
            List<CommentDto> commentDtos = result.getOrDefault(itemId, new ArrayList<>());
            commentDtos.add(toCommentDto(comment));
            result.put(itemId, commentDtos);
        });
        return result;
    }

    private Map<Long, Booking> getLastBookings(List<Booking> bookings, LocalDateTime currentTime) {
        log.debug("/getLastBookings");
        Map<Long, Booking> lastBookings = new HashMap<>(); //key = itemId
        bookings.stream()
                .filter(booking -> !booking.getStatus().equals(REJECTED))
                .filter(booking -> booking.getStart().isBefore(currentTime))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .forEach(booking -> {
                    Long itemId = booking.getItem().getId();
                    if (!lastBookings.containsKey(itemId)) lastBookings.put(itemId, booking);
                });
        return lastBookings;
    }

    private Map<Long, Booking> getNextBookings(List<Booking> bookings, LocalDateTime currentTime) {
        log.debug("/getNextBookings");
        Map<Long, Booking> nextBooking = new HashMap<>(); //key = itemId
        bookings.stream()
                .filter(booking -> !booking.getStatus().equals(REJECTED))
                .filter(booking -> booking.getStart().isAfter(currentTime))
                .sorted(Comparator.comparing(Booking::getStart))
                .forEach(booking -> {
                    Long itemId = booking.getItem().getId();
                    if (!nextBooking.containsKey(itemId)) nextBooking.put(itemId, booking);
                });
        return nextBooking;
    }
}