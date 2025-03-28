package rsreu.itemsharing.laba3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rsreu.itemsharing.laba3.entities.User3;

import java.sql.*;

@Controller
public class IndexController3l {
    private static final String URL = "jdbc:postgresql://localhost:5432/ItemSharingBD";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/lab3")
    public String lab3Page() {
        return "lab3";
    }

    @PostMapping("/lab3/addUser")
    public String addUser(Model model, String passportNum, String fullName, String phone, String email, String password, String address) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "{ ? = call add_user_return(?, ?, ?, ?, ?, ?) }";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.BOOLEAN);
                stmt.setLong(2, Long.parseLong(passportNum));
                stmt.setString(3, fullName);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setString(6, password);
                stmt.setString(7, address);
                stmt.execute();
                boolean success = stmt.getBoolean(1);
                model.addAttribute("message", success ? "User added successfully!" : "Error adding user.");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @PostMapping("/lab3/getUser")
    public String getUserByPassport(Model model, String passportNum) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "{ call get_user_by_passport(?) }";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setLong(1, Long.parseLong(passportNum));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        model.addAttribute("user", new User3(
                                rs.getLong("passport_num"),
                                rs.getString("full_name"),
                                rs.getString("phone").trim(),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("address")
                        ));
                    } else {
                        model.addAttribute("message", "User not found.");
                    }
                }
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @PostMapping("/lab3/updateUser")
    public String updateUser(Model model, String passportNum, String fullName, String phone, String email, String password, String address) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String getSql = "SELECT password FROM users WHERE passport_num = ?";
            String currentPassword;
            try (PreparedStatement stmt = conn.prepareStatement(getSql)) {
                stmt.setLong(1, Long.parseLong(passportNum));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        currentPassword = rs.getString("password");
                    } else {
                        model.addAttribute("message", "User not found.");
                        return "lab3";
                    }
                }
            }
            String encodedPassword = (password != null && !password.isEmpty()) ? passwordEncoder.encode(password) : currentPassword;
            String sql = "CALL update_user(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, Long.parseLong(passportNum));
                stmt.setString(2, fullName);
                stmt.setString(3, phone);
                stmt.setString(4, email);
                stmt.setString(5, encodedPassword);
                stmt.setString(6, address);
                stmt.executeUpdate();
                model.addAttribute("message", "User updated successfully!");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @PostMapping("/lab3/deleteUser")
    public String deleteUser(Model model, String passportNum) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String checkSql = "SELECT COUNT(*) FROM users WHERE passport_num = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setLong(1, Long.parseLong(passportNum));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        model.addAttribute("message", "User not found.");
                        return "lab3";
                    }
                }
            }
            String sql = "CALL delete_user(?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, Long.parseLong(passportNum));
                stmt.executeUpdate();
                model.addAttribute("message", "User deleted successfully!");
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }
}
