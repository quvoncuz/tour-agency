package quvoncuz.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class AgencyRepository {

    private final String FILE_NAME = "agency.csv";
    private static final String HEADER =
            "id,ownerId,name,phone,email,description,city,address,approved,rating,status";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void getMaxId() {
        List<AgencyEntity> existing = readFromFile();
        long maxId = existing.stream()
                .mapToLong(AgencyEntity::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    public void createOrUpdate(List<AgencyEntity> agencies, boolean isAppend) {
        rwLock.writeLock().lock();
        try {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
                if (!isAppend) {
                    writer.write(HEADER);
                    writer.newLine();
                }
                for (AgencyEntity a : agencies) {
                    if (a.getId() == null) {
                        a.setId(idGenerator.getAndIncrement());
                    }
                    writer.write(toCsvLine(a));
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    public List<AgencyEntity> getAllAgencies() {
        rwLock.readLock().lock();
        try {
            return readFromFile();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public AgencyEntity findById(Long agencyId) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream().filter(a -> a.getId().equals(agencyId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + agencyId));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public AgencyEntity findByOwnerId(Long ownerId) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .filter(a -> a.getOwnerId().equals(ownerId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Agency not found with owner id: " + ownerId));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private List<AgencyEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<AgencyEntity> bookings = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(FILE_NAME, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                AgencyEntity entity = fromCsvLine(line);
                if (entity != null) bookings.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException("booking.csv o'qishda xato: " + e.getMessage(), e);
        }
        return bookings;
    }

    private String toCsvLine(AgencyEntity a) {
        return a.getId() + "," +
                a.getOwnerId() + "," +
                escape(a.getName()) + "," +
                escape(a.getPhone()) + "," +
                escape(a.getEmail()) + "," +
                escape(a.getDescription()) + "," +
                escape(a.getCity()) + "," +
                escape(a.getAddress()) + "," +
                a.getApproved() + "," +
                a.getRating() + "," +
                a.getStatus();
    }

    private AgencyEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 12) return null;

        try {
            return new AgencyEntity(
                    Long.parseLong(s[0].trim()),
                    Long.parseLong(s[1].trim()),
                    unescape(s[2]),
                    unescape(s[3]),
                    unescape(s[4]),
                    unescape(s[5]),
                    unescape(s[6]),
                    unescape(s[7]),
                    Boolean.parseBoolean(s[8].trim()),
                    Double.parseDouble(s[9].trim()),
                    AgencyStatus.valueOf(s[10].trim())
            );
        } catch (Exception e) {
            System.err.println("Noto'g'ri qator o'tkazib yuborildi: " + line);
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
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.replace("\"\"", "\"");
    }

}
