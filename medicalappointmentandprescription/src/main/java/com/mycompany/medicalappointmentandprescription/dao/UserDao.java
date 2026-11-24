
package com.mycompany.medicalappointmentandprescription.dao;

import com.mycompany.medicalappointmentandprescription.models.User;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;
import com.mycompany.medicalappointmentandprescription.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

public class UserDao {

    // In-memory backup
    private static final List<User> ACCOUNTS = new ArrayList<>();

    private static int nextUserId = 4;

    // Initial backup accounts
    static {

        ACCOUNTS.add(new User(
                1,
                "admin",
                "Rahul Singh",
                "9876543214",
                "Admin",
                "adm123",
                true,
                true
        ));

        ACCOUNTS.add(new User(
                2,
                "staff",
                "Priya Sharma",
                "9876543215",
                "Staff",
                "rec123",
                true,
                true
        ));

        ACCOUNTS.add(new User(
                3,
                "doctor",
                "Dr Amit Verma",
                "9876543213",
                "Doctor",
                "doc123",
                true,
                true
        ));
    }

    // ==========================================
    // PASSWORD VERIFICATION
    // ==========================================

    private boolean passwordMatches(String password, String storedPassword) {

        if (password == null || storedPassword == null) {
            return false;
        }

        // BCrypt password
        if (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {

            return PasswordUtil.verifyPassword(password, storedPassword);
        }

        // Plain-text password for initial memory backup
        return storedPassword.equals(password);
    }

    // ==========================================
    // DATABASE TO MEMORY SYNCHRONIZATION
    // ==========================================

    private boolean syncAccountsFromDatabase() {

        List<User> databaseAccounts = new ArrayList<>();

        String sql = "SELECT user_id, username, full_name, "
                + "contact_number, role, password, is_active, "
                + "availability_status FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User user = new User();

                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                user.setIsActive(rs.getBoolean("is_active"));

                user.setAvaliabilityStatus(
                        rs.getBoolean("availability_status")
                );

                databaseAccounts.add(user);
            }

            synchronized (ACCOUNTS) {

                ACCOUNTS.clear();

                ACCOUNTS.addAll(databaseAccounts);

                nextUserId = 1;

                for (User user : ACCOUNTS) {

                    if (user.getUserId() >= nextUserId) {
                        nextUserId = user.getUserId() + 1;
                    }
                }
            }

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Database unavailable. Using in-memory backup."
            );

            return false;
        }
    }

    // ==========================================
    // VALIDATE LOGIN CREDENTIALS
    // ==========================================

    public User validateCredentials(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        syncAccountsFromDatabase();

        synchronized (ACCOUNTS) {

            for (User account : ACCOUNTS) {

                if (account.getUsername().equalsIgnoreCase(username.trim())
                        && account.getIsActive()
                        && passwordMatches(password, account.getPassword())) {

                    return account;
                }
            }
        }

        return null;
    }

    // ==========================================
    // FIND USER BY ID
    // ==========================================

    public User findByUserId(int userId) {

        syncAccountsFromDatabase();

        synchronized (ACCOUNTS) {

            for (User account : ACCOUNTS) {

                if (account.getUserId() == userId) {
                    return account;
                }
            }
        }

        return null;
    }

    // ==========================================
    // FIND USER BY USERNAME
    // ==========================================

    public User findByUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        syncAccountsFromDatabase();

        synchronized (ACCOUNTS) {

            for (User user : ACCOUNTS) {

                if (user.getUsername().equalsIgnoreCase(username.trim())) {
                    return user;
                }
            }
        }

        return null;
    }

    // ==========================================
    // FIND ALL USERS
    // ==========================================

    public List<User> findAll() {

        syncAccountsFromDatabase();

        synchronized (ACCOUNTS) {

            return new ArrayList<>(ACCOUNTS);
        }
    }

    // ==========================================
    // FIND ALL STAFF ACCOUNTS
    // ==========================================

    public List<User> findStaffAccount() {

        syncAccountsFromDatabase();

        List<User> staff = new ArrayList<>();

        synchronized (ACCOUNTS) {

            for (User user : ACCOUNTS) {

                if ("Staff".equals(user.getRole())) {
                    staff.add(user);
                }
            }
        }

        return staff;
    }

    // ==========================================
    // ADD USER
    // ==========================================

    public boolean addUser(User u) {

        if (u == null
                || u.getUsername() == null
                || u.getUsername().trim().isEmpty()
                || u.getFullName() == null
                || u.getFullName().trim().isEmpty()
                || u.getContactNumber() == null
                || u.getContactNumber().trim().isEmpty()
                || u.getPassword() == null
                || u.getPassword().trim().isEmpty()
                || u.getRole() == null
                || u.getRole().trim().isEmpty()) {

            return false;
        }

        if (findByUsername(u.getUsername()) != null) {
            return false;
        }

        String sql = "INSERT INTO users "
                + "(username, full_name, contact_number, role, "
                + "password, is_active, availability_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword =
                    PasswordUtil.hashPassword(u.getPassword());

            ps.setString(1, u.getUsername().trim());
            ps.setString(2, u.getFullName().trim());
            ps.setString(3, u.getContactNumber().trim());
            ps.setString(4, u.getRole());
            ps.setString(5, hashedPassword);
            ps.setBoolean(6, true);
            ps.setBoolean(7, u.getAvaliabilityStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        u.setUserId(rs.getInt(1));
                    }
                }

                u.setPassword(hashedPassword);
                u.setIsActive(true);

                syncAccountsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.out.println("Database unavailable. Adding user to memory.");

            synchronized (ACCOUNTS) {

                if (findByUsernameInMemory(u.getUsername()) != null) {
                    return false;
                }

                u.setUserId(nextUserId++);
                u.setIsActive(true);

                ACCOUNTS.add(u);

                return true;
            }
        }

        return false;
    }

    // ==========================================
    // FIND USER IN MEMORY ONLY
    // ==========================================

    private User findByUsernameInMemory(String username) {

        for (User user : ACCOUNTS) {

            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return user;
            }
        }

        return null;
    }

    // ==========================================
    // ADD STAFF
    // ==========================================

    public boolean addStaff(User u) {

        if (u == null || !"Staff".equals(u.getRole())) {
            return false;
        }

        return addUser(u);
    }

    // ==========================================
    // UPDATE USER
    // ==========================================

    public boolean updateUser(User u) {

        if (u == null
                || u.getUsername() == null
                || u.getUsername().trim().isEmpty()
                || u.getFullName() == null
                || u.getFullName().trim().isEmpty()
                || u.getContactNumber() == null
                || u.getContactNumber().trim().isEmpty()
                || u.getPassword() == null
                || u.getPassword().trim().isEmpty()
                || u.getRole() == null
                || u.getRole().trim().isEmpty()) {

            return false;
        }

        String sql = "UPDATE users SET full_name = ?, "
                + "contact_number = ?, is_active = ?, "
                + "availability_status = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getFullName());
            ps.setString(2, u.getContactNumber());
            ps.setBoolean(3, u.getIsActive());
            ps.setBoolean(4, u.getAvaliabilityStatus());
            ps.setInt(5, u.getUserId());

            int rows = ps.executeUpdate();

            if (rows > 0) {

                syncAccountsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.out.println("Database unavailable. Updating memory.");

            synchronized (ACCOUNTS) {

                User exist = findByUserIdInMemory(u.getUserId());

                if (exist == null) {
                    return false;
                }

                exist.setContactNumber(u.getContactNumber());
                exist.setFullName(u.getFullName());
                exist.setIsActive(u.getIsActive());
                exist.setAvaliabilityStatus(u.getAvaliabilityStatus());

                return true;
            }
        }

        return false;
    }

    private User findByUserIdInMemory(int userId) {

        for (User user : ACCOUNTS) {

            if (user.getUserId() == userId) {
                return user;
            }
        }

        return null;
    }

    // ==========================================
    // UPDATE PASSWORD
    // ==========================================

    public boolean updatePassword(int userId, String newPassword) {

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        String hashedPassword =
                PasswordUtil.hashPassword(newPassword);

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                syncAccountsFromDatabase();

                return true;
            }

        } catch (SQLException e) {

            System.out.println("Database unavailable. Updating memory.");

            synchronized (ACCOUNTS) {

                User user = findByUserIdInMemory(userId);

                if (user != null) {

                    user.setPassword(newPassword);

                    return true;
                }
            }
        }

        return false;
    }

    // ==========================================
    // DELETE ACCOUNT
    // ==========================================

    public boolean deleteAccount(int userId) {

        String sql = "DELETE FROM users "
                + "WHERE user_id = ? AND role <> 'Admin'";

        try (Connection con = DBConnection.getConnection()) {

            // Check registered patients
            String patientSql =
                    "SELECT COUNT(*) FROM patients WHERE registered_by = ?";

            try (PreparedStatement ps = con.prepareStatement(patientSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            // Check linked doctor
            String doctorSql =
                    "SELECT COUNT(*) FROM doctors WHERE user_id = ?";

            try (PreparedStatement ps = con.prepareStatement(doctorSql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, userId);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    syncAccountsFromDatabase();

                    return true;
                }
            }

        } catch (SQLException e) {

            System.out.println("Database unavailable. Deleting from memory.");

            synchronized (ACCOUNTS) {

                User user = findByUserIdInMemory(userId);

                if (user == null || "Admin".equals(user.getRole())) {
                    return false;
                }

                PatientDao pd = new PatientDao();

                for (Patient p : pd.findAll()) {

                    if (p.getRegisteredBy() == userId) {
                        return false;
                    }
                }

                DoctorDao dd = new DoctorDao();

                for (Doctor d : dd.findAll()) {

                    if (d.getUserId() == userId) {
                        return false;
                    }
                }

                return ACCOUNTS.remove(user);
            }
        }

        return false;
    }
}
