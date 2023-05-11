package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@NoArgsConstructor
@Component
public class CommentDtoMapper {

    public static ItemStorage itemStorage;
    public static UserStorage userStorage;

    @Autowired
    private CommentDtoMapper(ItemStorage itemStorage, UserStorage userStorage) {
        CommentDtoMapper.itemStorage = itemStorage;
        CommentDtoMapper.userStorage = userStorage;
    }

    public static Comment toComment(CommentDto commentDto, Long itemId, Long bookerId) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(itemStorage.getReferenceById(itemId));
        comment.setAuthor(userStorage.getReferenceById(bookerId));
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }
}