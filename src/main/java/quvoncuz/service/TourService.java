package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId);

    TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long ownerId);

    TourFullInfo updateTourPrice(Long tourId, Long newPrice, Long userId);

    Boolean deleteTour(Long tourId, Long ownerId);

    Page<TourShortInfo> getAllTour(int page, int size);

    Page<TourShortInfo> getAllActiveTour(int page, int size);

    TourFullInfo getById(Long id);

    Page<TourShortInfo> getAllSavedTours(Long userId, int page, int size);

    Page<TourShortInfo> search(String query, int page, int size);
}
