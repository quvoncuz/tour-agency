package quvoncuz.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.*;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto);

    TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto);

    TourFullInfo updateTourPrice(Long tourId, Long newPrice);

    Boolean deleteTour(Long tourId);

    Boolean cancelTour(Long tourId, CancelTourDTO reason);

    Page<TourShortInfo> getAllTour(int page, int size);

    Page<TourShortInfo> getAllActiveTour(int page, int size);

    TourFullInfo getById(Long id);

    Page<TourShortInfo> getAllSavedTours(int page, int size);

    Page<TourShortInfo> search(String query, int page, int size);
}
