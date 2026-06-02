package quvoncuz.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto);

    TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto);

    TourFullInfo updateTourPrice(Long tourId, Long newPrice);

    @Transactional
    Boolean cancelTour(Long tourId, String reason);

    Boolean deleteTour(Long tourId);

    Page<TourShortInfo> getAllTour(int page, int size);

    Page<TourShortInfo> getAllActiveTour(int page, int size);

    TourFullInfo getById(Long id);

    Page<TourShortInfo> getAllSavedTours(int page, int size);

    Page<TourShortInfo> search(String query, int page, int size);
}
