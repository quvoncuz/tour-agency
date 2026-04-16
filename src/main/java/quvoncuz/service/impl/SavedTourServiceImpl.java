package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.SavedTourService;

import java.util.List;

@Service
@Transactional
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
        savedTourRepository.save(savedTour);
        return true;
    }

    @Override
    public List<TourShortInfo> getAllSavedTours(Long userId, int page, int size) {
        List<Long> allSavedTourIdByUserId = savedTourRepository.findAllByUserId(userId)
                .stream()
                .map(SavedTourEntity::getTourId)
                .toList();
        return tourRepository.findAllByIdIn(allSavedTourIdByUserId, page - 1, size)
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

}
