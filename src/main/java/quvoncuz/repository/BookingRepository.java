package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final String FILE_NAME = "booking.csv";
    private static final String HEADER =
            "id,userId,tourId,seatsBooked,totalPrice,status,note,bookedAt";

    public long getMaxId() {
        List<BookingEntity> existing = readFromFile();
        return existing.stream()
                .mapToLong(BookingEntity::getId)
                .max()
                .orElse(0L) + 1;
    }

    public void createOrUpdate(List<BookingEntity> bookings, boolean isAppend) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            if (!isAppend){
                writer.write(HEADER);
                writer.newLine();
            }
            for (BookingEntity b : bookings) {
                if (b.getId() == null) {
                    b.setId(getMaxId());
                }
                writer.write(toCsvLine(b));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<BookingEntity> findAll() {
        return readFromFile();
    }

    public Optional<BookingEntity> findById(Long id) {
        return readFromFile().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    public List<BookingEntity> findByUserId(Long userId) {
        return readFromFile()
                .stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .toList();
    }

    public List<BookingEntity> findByTourId(Long tourId) {
        return readFromFile()
                .stream()
                .filter(booking -> booking.getTourId().equals(tourId))
                .toList();
    }

    public List<BookingEntity> findByStatus(BookingStatus status) {
        return readFromFile().stream()
                .filter(booking -> booking.getStatus().equals(status))
                .toList();
    }

    private List<BookingEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<BookingEntity> bookings = new LinkedList<>();
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
                BookingEntity entity = fromCsvLine(line);
                if (entity != null) bookings.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return bookings;
    }

    private String toCsvLine(BookingEntity b) {
        return b.getId() + "," +
                b.getUserId() + "," +
                b.getTourId() + "," +
                b.getSeatsBooked() + "," +
                b.getTotalPrice() + "," +
                b.getStatus() + "," +
                escape(b.getNote()) + "," +
                b.getBookedAt();
    }

    private BookingEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 8) {
            return null;
        }
        try {
            return new BookingEntity(
                    Long.parseLong(s[0].trim()),
                    Long.parseLong(s[1].trim()),
                    Long.parseLong(s[2].trim()),
                    Integer.parseInt(s[3].trim()),
                    new BigDecimal(s[4].trim()),
                    BookingStatus.valueOf(s[5].trim()),
                    unescape(s[6]),
                    LocalDateTime.parse(s[7].trim())
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
