package quvoncuz.service;

import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;

import java.util.List;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId);

    TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long ownerId);

    Boolean deleteTour(Long tourId, Long ownerId);

    List<TourShortInfo> getAllTour(int page, int size);

    List<TourShortInfo> getAllActiveTour(int page, int size);

    TourFullInfo getById(Long id);

    List<TourShortInfo> getAllSavedTours(Long userId, int page, int size);

    List<TourShortInfo> search(String query, int page, int size);
}
