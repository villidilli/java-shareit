package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.utils.PageConfig;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {
    public static final Sort sortByIdDesc = Sort.by("id").descending();
    public static final Sort sortByIdAsc = Sort.by("id").ascending();
}
