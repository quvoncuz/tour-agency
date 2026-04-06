package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        savedTourRepository.save(savedTour);
        return true;
    }

    @Override
    public Page<TourShortInfo> getAllSavedTours(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Long> allSavedTourIdByUserId = savedTourRepository.findAllByUserId(userId)
                .stream()
                .map(SavedTourEntity::getTourId)
                .toList();
        return tourRepository.findAllByIdIn(allSavedTourIdByUserId, pageRequest)
                .map(TourMapper::toShortInfo);
    }

}
