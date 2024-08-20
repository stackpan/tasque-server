package io.github.stackpan.tasque.service.util;

import io.github.stackpan.tasque.entity.BoardColumn;
import io.github.stackpan.tasque.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ColumnServiceUtil {

    private final ColumnRepository columnRepository;

    public BoardColumn findByIdOrThrowsNotFound(UUID columnId) {
        return columnRepository.findById(columnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
