package ru.practicum.shareit.item.dto;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemDtoMapper implements RowMapper<Item> {
	@Override
	public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
		return null;
	}
}
