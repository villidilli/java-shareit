package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {
    public static final Sort sortByIdDesc = Sort.by("id").descending();
    public static final Sort sortByIdAsc = Sort.by("id").ascending();
    public static final String PARAM_NAME_BOOKING_STATE = "state";
    public static final String DEFAULT_BOOKING_STATE = "ALL";
    public static final String PARAM_APPROVED = "approved";
    public static final String PARAM_USER_ID = "X-Sharer-User-Id";
    public static final String FIRST_PAGE = "from";
    public static final String DEFAULT_FIRST_PAGE = "0";
    public static final String SIZE_VIEW = "size";
    public static final String DEFAULT_SIZE_VIEW = "999";
    public static final Sort sortByCreatedDesc = Sort.by("created").descending();

}
