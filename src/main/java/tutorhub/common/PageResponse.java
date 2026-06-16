package tutorhub.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A stable JSON shape for paged results. We map Spring Data's Page into this
 * rather than returning Page directly, because Page's own JSON format is
 * considered unstable by Spring and logs a warning if serialized as-is.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}