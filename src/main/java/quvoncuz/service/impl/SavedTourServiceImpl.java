package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.service.SavedTourService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedTourServiceImpl implements SavedTourService {

    private final SavedTourRepository savedTourRepository;

    public boolean saveTour(Long userId, Long tourId) {
        if (savedTourRepository.existsByTourIdAndUserId(tourId, userId)) {
            return savedTourRepository.deleteByTourIdAndUserId(tourId, userId);
        }
        SavedTourEntity savedTour = new SavedTourEntity();
        savedTour.setUserId(userId);
        savedTour.setTourId(tourId);
        savedTourRepository.createOrUpdate(List.of(savedTour), true);
        return true;
    }

}
