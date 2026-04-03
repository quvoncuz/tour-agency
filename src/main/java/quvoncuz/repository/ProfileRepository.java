package quvoncuz.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class ProfileRepository {

    private final String FILE_FOLDER;
    private final String FILE_NAME;
    private static final String HEADER =
            "id,fullName,username,email,password,balance,role,gender,isCreateAgency,isActive";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProfileRepository(@Value("${file.folder}") String FILE_FOLDER) {
        this.FILE_FOLDER = FILE_FOLDER;
        this.FILE_NAME = FILE_FOLDER + "profiles.csv";
    }

    @PostConstruct
    public void getMaxId() {
        List<ProfileEntity> existing = readFromFile();
        long maxId = existing.stream()
                .mapToLong(ProfileEntity::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    public void createOrReplace(List<ProfileEntity> profiles, boolean isAppend) {
        File file = new File(FILE_NAME);
        File folder = new File(FILE_FOLDER);
        if (!file.exists()) {
            folder.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        rwLock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, isAppend))) {
            for (ProfileEntity p : profiles) {
                if (p.getId() == null) {
                    p.setId(idGenerator.getAndIncrement());
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

    public List<ProfileEntity> findAll() {
        rwLock.readLock().lock();
        try {
            return readFromFile();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public ProfileEntity findById(Long id) {

        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Profile not found with id: " + id));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Optional<ProfileEntity> findByUsername(String username) {
        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(p -> p.getUsername().equals(username))
                    .findFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Optional<ProfileEntity> findByEmail(String email) {

        rwLock.readLock().lock();
        try {
            return readFromFile().stream()
                    .filter(p -> p.getEmail().equals(email))
                    .findFirst();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean existsByUsername(String username) {
        rwLock.readLock().lock();
        try {
            return findByUsername(username).isPresent();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean existsByEmail(String email) {
        rwLock.readLock().lock();
        try {
            return findByEmail(email).isPresent();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean deleteById(Long id) {
        rwLock.writeLock().lock();
        try {
            List<ProfileEntity> all = readFromFile();
            boolean removed = all.removeIf(p -> p.getId().equals(id));
            if (removed) createOrReplace(all, false);
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private List<ProfileEntity> readFromFile() {
        File file = new File(FILE_NAME);
        File folder = new File(FILE_FOLDER);
        if (!file.exists()) {
            folder.mkdirs();
            try {
                file.createNewFile();
                return new LinkedList<>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<ProfileEntity> profiles = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(FILE_NAME, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                ProfileEntity entity = fromCsvLine(line);
                if (entity != null) profiles.add(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return profiles;
    }

    private String toCsvLine(ProfileEntity p) {
        return p.getId() + "," +
                escape(p.getFullName()) + "," +
                escape(p.getUsername()) + "," +
                escape(p.getEmail()) + "," +
                escape(p.getPassword()) + "," +
                p.getBalance() + "," +
                p.getRole() + "," +
                p.getGender() + "," +
                p.getIsCreateAgency() + "," +
                p.getIsActive();
    }

    private ProfileEntity fromCsvLine(String line) {
        String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (s.length != 10) {
            System.err.println("profiles.csv — noto'g'ri ustun soni: " + line);
            return null;
        }
        try {
            return new ProfileEntity(
                    Long.parseLong(s[0].trim()),
                    unescape(s[1]),
                    unescape(s[2]),
                    unescape(s[3]),
                    unescape(s[4]),
                    Long.parseLong(s[5].trim()),
                    Role.valueOf(s[6].trim()),
                    Gender.valueOf(s[7].trim()),
                    Boolean.parseBoolean(s[8].trim()),  // ✅ getBoolean emas!
                    Boolean.parseBoolean(s[9].trim())   // ✅ getBoolean emas!
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
