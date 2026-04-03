package quvoncuz.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class RatingRepository {

    private final String FILE_NAME;
    private static final String HEADER =
            "id,userId,sourceId,stars,comment,target,createdAt";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public RatingRepository(@Value("${file.folder}") String FILE_FOLDER) {
        this.FILE_NAME = FILE_FOLDER + "ratings.csv";
    }

    @PostConstruct
    public void getMaxId() {
        List<RatingEntity> existing = readFromFile();
        long maxId = existing.stream()
                .mapToLong(RatingEntity::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    public void createOrReplace(List<RatingEntity> ratings, boolean isAppend) {
        rwLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            for (RatingEntity r : ratings) {
                if (r.getId() == null) {
                    r.setId(idGenerator.getAndIncrement());
                }
                writer.write(toCsvLine(r));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<RatingEntity> findAll() {
        rwLock.readLock().lock();
        try {
            return readFromFile();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Optional<RatingEntity> findById(Long id) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public boolean deleteById(Long id) {
        rwLock.writeLock().lock();
        try {
            List<RatingEntity> all = readFromFile();
            boolean removed = all.removeIf(r -> r.getId().equals(id));
            if (removed) writeToFile(all);
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<RatingEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<RatingEntity> ratings = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(FILE_NAME, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                RatingEntity entity = fromCsvLine(line);
                if (entity != null) ratings.add(entity);
            }
        } catch (IOException e) {
            return new LinkedList<>();
        }
        return ratings;
    }

    private void writeToFile(List<RatingEntity> ratings) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, false))) {
            writer.write(HEADER);
            writer.newLine();
            for (RatingEntity r : ratings) {
                writer.write(toCsvLine(r));
                writer.newLine();
            }
        } catch (IOException e) {
            return;
        }
    }

    private String toCsvLine(RatingEntity r) {
        return r.getId() + "," +
                r.getUserId() + "," +
                r.getSourceId() + "," +
                r.getType() + "," +
                r.getStars() + "," +
                escape(r.getComment()) + "," +
                r.getCreatedAt();
    }

    private RatingEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 7) {
            return null;
        }
        try {
            return new RatingEntity(
                    Long.parseLong(s[0].trim()),
                    Long.parseLong(s[1].trim()),
                    Long.parseLong(s[2].trim()),
                    RatingType.valueOf(s[5].trim()),
                    Integer.parseInt(s[3].trim()),
                    unescape(s[4]),
                    LocalDateTime.parse(s[6].trim())
            );
        } catch (Exception e) {
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