package quvoncuz.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class TourRepository {


    private static final String FILE_NAME = "tours.csv";
    private static final String HEADER =
            "id,agencyId,title,description,destination,price," +
                    "durationDays,maxSeats,availableSeats,startDate,endDate," +
                    "isActive,viewCount,rating,createdDate";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void getMaxId() {
        List<TourEntity> existing = readFromFile();
        long maxId = existing.stream()
                .mapToLong(TourEntity::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }
    public void createOrUpdate(List<TourEntity> tours, boolean isAppend) {
        rwLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            if (!isAppend) {
                writer.write(HEADER);
                writer.newLine();
            }
            for (TourEntity t : tours) {
                if (t.getId() == null) {
                    t.setId(idGenerator.getAndIncrement());
                }
                writer.write(toCsvLine(t));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<TourEntity> findAll() {
        rwLock.readLock().lock();
        try {
            return readFromFile();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public TourEntity findById(Long id) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Tour not found"));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<TourEntity> findByAgencyId(Long agencyId) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(tour -> tour.getAgencyId().equals(agencyId))
                    .toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean deleteById(Long id) {
        rwLock.writeLock().lock();
        try {
            List<TourEntity> all = readFromFile();
            boolean removed = all.removeIf(t -> t.getId().equals(id));
            if (removed) createOrUpdate(all, false);
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<TourEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<TourEntity> tours = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(FILE_NAME, StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                TourEntity entity = fromCsvLine(line);
                if (entity != null) tours.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return tours;
    }

    private String toCsvLine(TourEntity t) {
        return t.getId() + "," +
                t.getAgencyId() + "," +
                escape(t.getTitle()) + "," +
                escape(t.getDescription()) + "," +
                escape(t.getDestination()) + "," +
                t.getPrice() + "," +
                t.getDurationDays() + "," +
                t.getMaxSeats() + "," +
                t.getAvailableSeats() + "," +
                t.getStartDate() + "," +
                t.getEndDate() + "," +
                t.getIsActive() + "," +
                t.getViewCount() + "," +
                t.getRating() + "," +
                t.getStatus() + "," +
                t.getCreatedDate();
    }

    private TourEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 16) {
            return null;
        }
        try {
            return new TourEntity(
                    Long.parseLong(s[0].trim()),
                    Long.parseLong(s[1].trim()),
                    unescape(s[2]),
                    unescape(s[3]),
                    unescape(s[4]),
                    new BigDecimal(s[5].trim()),
                    Integer.parseInt(s[6].trim()),
                    Integer.parseInt(s[7].trim()),
                    Integer.parseInt(s[8].trim()),
                    LocalDate.parse(s[9].trim()),
                    LocalDate.parse(s[10].trim()),
                    Boolean.parseBoolean(s[11].trim()),
                    Long.parseLong(s[12].trim()),
                    Double.parseDouble(s[13].trim()),
                    TourStatus.valueOf(s[14].trim()),
                    LocalDateTime.parse(s[15].trim())
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private String escape(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String unescape(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\""))
            value = value.substring(1, value.length() - 1);
        return value.replace("\"\"", "\"");
    }
}
