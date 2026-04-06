package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;

public interface SavedTourService {

    Boolean saveTour(SaveTourRequestDTO dto, Long userId);

    Page<TourShortInfo> getAllSavedTours(Long userId, int page, int size);
}
