package quvoncuz.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class SavedTourRepository {
    private static final String FILE_NAME = "savedtours.csv";
    private static final String HEADER =
            "id,userId,tourId,createdAt";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void getMaxId() {
        List<SavedTourEntity> existing = readFromFile();
        long maxId = existing.stream()
                .mapToLong(SavedTourEntity::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    public void createOrUpdate(List<SavedTourEntity> savedTours, boolean isAppend) {
        rwLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            if (!isAppend) {
                writer.write(HEADER);
                writer.newLine();
            }
            for (SavedTourEntity t : savedTours) {
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

    public List<SavedTourEntity> findAllByUserId(Long userId) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .filter(t -> t.getUserId().equals(userId))
                    .toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean existsByTourIdAndUserId(Long tourId, Long UserId) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .anyMatch(t -> t.getTourId().equals(tourId) && t.getUserId().equals(UserId));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean deleteByTourIdAndUserId(Long tourId, Long userId) {
        rwLock.writeLock().lock();
        try {
            List<SavedTourEntity> all = readFromFile();
            boolean removed = all
                    .removeIf(t -> t.getTourId().equals(tourId)
                            && t.getUserId().equals(userId));
            if (removed) createOrUpdate(all, false);
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<SavedTourEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<SavedTourEntity> tours = new LinkedList<>();
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
                SavedTourEntity entity = fromCsvLine(line);
                if (entity != null) tours.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return tours;
    }

    private String toCsvLine(SavedTourEntity t) {
        return t.getId() + "," +
                t.getUserId() + "," +
                t.getTourId() + "," +
                t.getCreatedAt();
    }

    private SavedTourEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 4) {
            return null;
        }
        return new SavedTourEntity(
                Long.parseLong(s[0]),
                Long.parseLong(s[1]),
                Long.parseLong(s[2]),
                LocalDateTime.parse(s[3])
        );
    }
}
