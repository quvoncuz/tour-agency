package quvoncuz.repository;

import org.springframework.stereotype.Repository;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Gender;
import quvoncuz.enums.Role;

import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ProfileRepository {

    private final String FILE_NAME = "profiles.csv";
    private final String FILE_PATH = "resources";

    public void create(List<ProfileEntity> profiles, boolean isAppend) {

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
                writer.write("id,fullName,username,email,password,role,gender,isCreateAgency,isActive");
            }
            writer.newLine();

            for (ProfileEntity profile : profiles) {
                writer.write(
                        profile.getId() + "," +
                                profile.getFullName() + "," +
                                profile.getUsername() + "," +
                                profile.getEmail() + "," +
                                profile.getPassword() + "," +
                                profile.getBalance() + "," +
                                profile.getRole() + "," +
                                profile.getGender() + "," +
                                profile.getIsCreateAgency() + "," +
                                profile.getIsActive());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<ProfileEntity> getAllProfile() {
        List<ProfileEntity> profiles = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] splits = line.split(",");
                if (splits.length == 10) {
                    Long id = Long.parseLong(splits[0]);
                    String fullName = splits[1];
                    String username = splits[2];
                    String email = splits[3];
                    String password = splits[4];
                    BigDecimal balance = BigDecimal.valueOf(Long.parseLong(splits[5]));
                    Role role = Role.valueOf(splits[6]);
                    Gender gender = Gender.valueOf(splits[7]);
                    Boolean isCreateAgency = Boolean.getBoolean(splits[8]);
                    Boolean isActive = Boolean.getBoolean(splits[9]);
                    profiles.add(new ProfileEntity(id, fullName, username, email, password, balance, role, gender, isCreateAgency, isActive));
                }
            }
            return profiles;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
