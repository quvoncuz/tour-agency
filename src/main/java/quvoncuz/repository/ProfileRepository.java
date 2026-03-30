package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProfileRepository {

    private static final String FILE_NAME = "profiles.csv";
    private static final String HEADER =
            "id,fullName,username,email,password,balance,role,gender,isCreateAgency,isActive";

    public long getMaxId() {
        List<ProfileEntity> existing = readFromFile();
        return existing.stream()
                .mapToLong(ProfileEntity::getId)
                .max()
                .orElse(0L) + 1;
    }

    public void createOrReplace(List<ProfileEntity> profiles, boolean isAppend) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8, false))) {
            writer.write(HEADER);
            writer.newLine();
            for (ProfileEntity p : profiles) {
                if (p.getId() == null) {
                    p.setId(getMaxId());
                }
                writer.write(toCsvLine(p));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ProfileEntity> findAll() {

        return readFromFile();
    }

    public ProfileEntity findById(Long id) {

        return readFromFile().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Profile not found with id: " + id));
    }

    public Optional<ProfileEntity> findByUsername(String username) {

        return readFromFile().stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst();
    }

    public Optional<ProfileEntity> findByEmail(String email) {

        return readFromFile().stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean deleteById(Long id) {
        List<ProfileEntity> all = readFromFile();
        boolean removed = all.removeIf(p -> p.getId().equals(id));
        if (removed) createOrReplace(all, false);
        return removed;
    }

    private List<ProfileEntity> readFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<ProfileEntity> profiles = new LinkedList<>();
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
                    new BigDecimal(s[5].trim()),
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
