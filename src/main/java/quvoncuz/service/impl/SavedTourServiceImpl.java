package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.SavedTourService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedTourServiceImpl implements SavedTourService {

    private final SavedTourRepository savedTourRepository;
    private final TourRepository tourRepository;

    @Override
    public Boolean saveTour(SaveTourRequestDTO dto, Long userId) {
        if (savedTourRepository.existsByTourIdAndUserId(dto.getTourId(), userId)) {
            return savedTourRepository.deleteByTourIdAndUserId(dto.getTourId(), userId);
        }
        SavedTourEntity savedTour = new SavedTourEntity();
        savedTour.setUserId(userId);
        savedTour.setTourId(dto.getTourId());
        savedTourRepository.createOrUpdate(List.of(savedTour), true);
        return true;
    }

    @Override
    public List<TourShortInfo> getAllSavedTours(Long userId) {
        List<Long> allSavedTourIdByUserId = savedTourRepository.findAllByUserId(userId)
                .stream()
                .map(SavedTourEntity::getTourId)
                .toList();
        return tourRepository.findAll()
                .stream()
                .filter(tour -> allSavedTourIdByUserId.contains(tour.getId()))
                .map(TourMapper::toShortInfo)
                .toList();
    }

}
