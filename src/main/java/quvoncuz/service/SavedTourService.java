package quvoncuz.service;

import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;

import java.util.List;

public interface SavedTourService {
    Boolean saveTour(SaveTourRequestDTO dto, Long userId);

    List<TourShortInfo> getAllSavedTours(Long userId);
}
