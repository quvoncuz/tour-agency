package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class PaymentRepository {
    private final String FILE_NAME = "booking.csv";
    private static final String HEADER =
            "id,userId,tourId,bookingId,amount,status,createdAt";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public long getMaxId() {
        rwLock.readLock().lock();
        try {
            List<PaymentEntity> existing = readFromFile();
            return existing.stream()
                    .mapToLong(PaymentEntity::getId)
                    .max()
                    .orElse(0L) + 1;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void createOrUpdate(List<PaymentEntity> payments, boolean isAppend) {
        rwLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            if (!isAppend){
                writer.write(HEADER);
                writer.newLine();
            }
            for (PaymentEntity p : payments) {
                if (p.getId() == null) {
                    p.setId(getMaxId());
                }
                writer.write(toCsvLine(p));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<PaymentEntity> findAll() {
        rwLock.readLock().lock();
        try {
            return readFromFile();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Optional<PaymentEntity> findById(Long id) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(b -> b.getId().equals(id))
                    .findFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PaymentEntity> findByUserId(Long userId) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .filter(booking -> booking.getUserId().equals(userId))
                    .toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PaymentEntity> findByTourId(Long tourId) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .filter(booking -> booking.getTourId().equals(tourId))
                    .toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public List<PaymentEntity> findByStatus(PaymentStatus status) {
        rwLock.readLock().lock();
        try {
            return readFromFile()
                    .stream()
                    .filter(booking -> booking.getStatus().equals(status))
                    .toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private List<PaymentEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<PaymentEntity> bookings = new LinkedList<>();
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
                PaymentEntity entity = fromCsvLine(line);
                if (entity != null) bookings.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return bookings;
    }

    private String toCsvLine(PaymentEntity b) {
        return b.getId() + "," +
                b.getUserId() + "," +
                b.getTourId() + "," +
                b.getBookingId() + "," +
                b.getAmount() + "," +
                b.getStatus() + "," +
                b.getCreatedAt();
    }

    private PaymentEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 8) {
            return null;
        }
        try {
            return new PaymentEntity(
                    Long.parseLong(s[0].trim()),
                    Long.parseLong(s[1].trim()),
                    Long.parseLong(s[2].trim()),
                    Long.parseLong(s[2].trim()),
                    new BigDecimal(s[3].trim()),
                    PaymentStatus.valueOf(s[5].trim()),
                    LocalDateTime.parse(s[7].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }
}
