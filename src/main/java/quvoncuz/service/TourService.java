package quvoncuz.service;

import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;

import java.util.List;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId);

    TourFullInfo updateTour(UpdateTourRequestDTO dto, Long ownerId);

    boolean deleteTour(Long tourId, Long ownerId);

    List<TourShortInfo> getAllTour();

    List<TourShortInfo> getAllActiveTour();

    TourFullInfo getById(Long id);

    List<TourShortInfo> getAllSavedTour(Long userId, int size, int page);

    List<TourShortInfo> search(String query);
}
