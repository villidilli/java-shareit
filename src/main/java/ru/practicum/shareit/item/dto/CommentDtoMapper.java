package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
@Slf4j
public class CommentDtoMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        log.debug("/toComment");
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        log.debug("/toCommentDto");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }
}