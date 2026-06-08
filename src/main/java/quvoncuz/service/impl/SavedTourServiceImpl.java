package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.SavedTourService;

@Service
@RequiredArgsConstructor
public class SavedTourServiceImpl implements SavedTourService {

    private final SavedTourRepository savedTourRepository;
    private final TourRepository tourRepository;

    @Override
    @Transactional
    public void saveTour(SaveTourRequestDTO dto, Long userId) {
        if (savedTourRepository.existsByTourIdAndUserId(dto.getTourId(), userId)) {
            savedTourRepository.deleteByTourIdAndUserId(dto.getTourId(), userId);
        }
        SavedTourEntity savedTour = new SavedTourEntity();
        savedTour.setUserId(userId);
        savedTour.setTourId(dto.getTourId());
        savedTourRepository.save(savedTour);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TourShortInfo> getAllSavedTours(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return tourRepository.findSavedToursByUserId(userId, pageRequest)
                .map(TourMapper::toShortInfo);
    }

}
