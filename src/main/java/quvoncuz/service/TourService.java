package quvoncuz.service;

import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;

import java.util.List;

public interface TourService {
    public TourFullInfo createTour(CreateTourRequestDTO dto);
    public TourFullInfo updateTour(UpdateTourRequestDTO dto);
    public boolean deleteTour(Long tourId);
    public List<TourShortInfo> getAllTour();
    public TourFullInfo getById(Long id);
    public List<TourShortInfo> search(String query);
}
