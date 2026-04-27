package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;

public interface SavedTourService {

    Boolean saveTour(SaveTourRequestDTO dto);

    Page<TourShortInfo> getAllSavedTours(int page, int size);
}
