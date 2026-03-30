package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.NotFoundException;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

@Repository
public class AgencyRepository {

    private final String FILE_NAME = "agency.csv";
    private static final String HEADER =
            "id,ownerId,name,phone,email,description,city,address,approved,rating,status";

    public void createOrUpdate(List<AgencyEntity> agencies, boolean isAppend) {
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
                writer.write("id,ownerId,name,phone,email,description,city,address,approved,rating,visitedCount,status");
                writer.newLine();
            }

            for (AgencyEntity agency : agencies) {
                writer.write(toCsvLine(agency));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AgencyEntity> getAllAgencies() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new LinkedList<>();

        List<AgencyEntity> agencies = new LinkedList<>();

        String line;
        boolean firstLine = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                AgencyEntity entity = fromCsvLine(line);
                if (entity != null) agencies.add(entity);
            }
            return agencies;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AgencyEntity findById(Long agencyId){
        return getAllAgencies().stream().filter(a -> a.getId().equals(agencyId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found with id: " + agencyId));
    }

    public AgencyEntity findByOwnerId(Long ownerId){
        return getAllAgencies()
                .stream()
                .filter(a -> a.getOwnerId().equals(ownerId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found with owner id: " + ownerId));
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
