package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageConfig extends PageRequest {

    public PageConfig(Integer from, Integer size, Sort sort) {
        super(getFirstPage(from, size), size, sort);
    }

    private static int getFirstPage(int from, int size) {
        return from != 0 ? from / size : 0;
    }
}