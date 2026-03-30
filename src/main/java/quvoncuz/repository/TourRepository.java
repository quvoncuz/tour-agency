package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.TourEntity;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Repository
public class TourRepository {

    private final String FILE_NAME = "tours.csv";
    private final String FILE_PATH = "resource";

    public void createOrUpdate(List<TourEntity> tours, boolean isAppend) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, isAppend))) {
            if (!isAppend) {
                writer.write("""
                        id,
                        agencyId,
                        title,
                        description,
                        destination,
                        price,
                        durationDays,
                        maxSeats,
                        availableSeats,
                        startDate,
                        endDate,
                        isActive,
                        viewCount,
                        rating,
                        createdDate""");
            }
            writer.newLine();

            for (TourEntity tour : tours) {
                writer.write(
                        tour.getId() + "," +
                                tour.getAgencyId() + "," +
                                tour.getTitle() + "," +
                                tour.getDescription() + "," +
                                tour.getDestination() + "," +
                                tour.getPrice() + "," +
                                tour.getDurationDays() + "," +
                                tour.getMaxSeats() + "," +
                                tour.getAvailableSeats() + "," +
                                tour.getStartDate() + "," +
                                tour.getEndDate() + "," +
                                tour.getIsActive() + "," +
                                tour.getViewCount() + "," +
                                tour.getRating() + "," +
                                tour.getCreatedDate());
            }
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TourEntity> getAllTour() {
        List<TourEntity> tours = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] splits = line.split(",");
                if (splits.length == 15) {
                    Long id = Long.parseLong(splits[0]);
                    Long agencyId = Long.parseLong(splits[1]);
                    String title = splits[2];
                    String description = splits[3];
                    String destination = splits[4];
                    BigDecimal price = BigDecimal.valueOf(Long.parseLong(splits[5]));
                    Integer durationDays = Integer.valueOf(splits[6]);
                    Integer maxSeats = Integer.valueOf(splits[7]);
                    Integer availableSeats = Integer.valueOf(splits[8]);
                    LocalDate startDate = LocalDate.parse(splits[9]);
                    LocalDate endDate = LocalDate.parse(splits[10]);
                    Boolean isActive = Boolean.valueOf(splits[11]);
                    Long viewCount = Long.parseLong(splits[12]);
                    Double rating = Double.valueOf(splits[13]);
                    LocalDateTime createdDate = LocalDateTime.parse(splits[14]);
                    tours.add(new TourEntity(id,
                            agencyId,
                            title,
                            description,
                            destination,
                            price,
                            durationDays,
                            maxSeats,
                            availableSeats,
                            startDate,
                            endDate,
                            isActive,
                            viewCount,
                            rating,
                            createdDate));
                }
            }
            return tours;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean deleteById(Long id) {
        List<TourEntity> allTour = getAllTour();
        TourEntity tour1 = allTour.stream()
                .filter(tour -> tour.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tour with id " + id + " not found!"));
        allTour.remove(tour1);
        createOrUpdate(allTour, false);
        return true;
    }

    public TourEntity getTourById(Long id) {
        return getAllTour().stream().filter(tour -> tour.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("tour not found"));
    }
}
