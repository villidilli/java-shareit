package ru.practicum.shareit.user.dto;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDtoMapper implements RowMapper<User> {
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		return null;
	}
}
