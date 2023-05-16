package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequester_Id(Long requesterId, Sort sort);

//    List<ItemRequest> findByRequester_IdNot(Long requesterId);
    Page<ItemRequest> findByRequester_IdNot(Long requesterId, Pageable page);

    ItemRequest findByIdIs(Long requestId);
}