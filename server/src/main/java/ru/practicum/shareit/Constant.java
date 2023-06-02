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
    public static final String PARAM_NAME_BOOKING_STATE = "state";
    public static final String DEFAULT_BOOKING_STATE = "ALL";
    public static final String PARAM_APPROVED = "approved";
    public static final String PARAM_USER_ID = "X-Sharer-User-Id";
}
