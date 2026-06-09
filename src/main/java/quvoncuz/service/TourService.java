package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.tour.*;

public interface TourService {

    TourFullInfo createTour(CreateTourRequestDTO dto, Long userId);

    TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long userId);

    void deleteTour(Long tourId, Long userId);

    void cancelTour(Long tourId, CancelTourDTO reason, Long userId);

    Page<TourShortInfo> getAllTourForAdmin(int page, int size);

    Page<TourShortInfo> getAllTourForAgency(Long userId, int page, int size);

    Page<TourShortInfo> getAllActiveTour(int page, int size);

    TourFullInfo getById(Long id);

    Page<TourShortInfo> search(String query, int page, int size);
}
