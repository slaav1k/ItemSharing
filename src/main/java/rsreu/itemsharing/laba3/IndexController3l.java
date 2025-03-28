package rsreu.itemsharing.laba3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.laba3.entities.User3;

import java.sql.*;

@Controller
public class IndexController3l {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Проверка добавления пользователя через хранимую функцию
    @GetMapping("/lab3")
    public String lab3Page(Model model) {
        // Отображение пустой страницы или текста для ввода данных
        return "lab3";
    }

    // Пример вызова функции для добавления пользователя
    @PostMapping("/lab3/addUser")
    public String addUser(Model model, String passportNum, String fullName, String phone, String email, String password, String address) {
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            // Подготовка вызова функции
            String sql = "{ ? = call add_user_return(?, ?, ?, ?, ?, ?) }";

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Установка параметров
                stmt.setLong(2, Long.parseLong(passportNum));  // passportNum - Long
                stmt.setString(3, fullName);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setString(6, password);
                stmt.setString(7, address);

                // Регистрация возвращаемого значения (BOOLEAN)
                stmt.registerOutParameter(1, Types.BOOLEAN);

                // Выполнение функции
                stmt.execute();

                // Получаем результат
                Boolean success = stmt.getBoolean(1);

                if (success != null && success) {
                    model.addAttribute("message", "User added successfully!");
                } else {
                    model.addAttribute("message", "Error adding user.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @PostMapping("/lab3/getUser")
    public String getUserByPassport(Model model, String passportNum) {
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            // Подготовка вызова функции get_user_by_passport
            String sql = "{ call get_user_by_passport(?) }";

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Установка параметров
                stmt.setLong(1, Long.parseLong(passportNum));  // passportNum - Long

                // Выполнение функции
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Извлекаем данные пользователя из результата запроса
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
            } catch (SQLException e) {
                throw new RuntimeException("Error executing stored function", e);
            }
        } catch (SQLException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/lab3/updateUser")
    public String updateUser(Model model,
                             @RequestParam String passportNum,
                             @RequestParam String fullName,
                             @RequestParam String phone,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam String address) {
        try {
            long passportLong = Long.parseLong(passportNum);

            // Получаем текущего пользователя
            String getSql = "SELECT password FROM users WHERE passport_num = ?";
            String currentEncodedPassword = jdbcTemplate.queryForObject(getSql, String.class, passportLong);

            // Хешируем новый пароль только если он предоставлен
            String encodedPassword = (password != null && !password.isEmpty())
                    ? passwordEncoder.encode(password)
                    : currentEncodedPassword;

            // Правильный вызов процедуры
            String sql = "CALL update_user(?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    passportLong,
                    fullName,
                    phone,
                    email,
                    encodedPassword,
                    address);

            model.addAttribute("message", "User updated successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "lab3";
    }

    @PostMapping("/lab3/deleteUser")
    public String deleteUser(Model model, @RequestParam String passportNum) {
        try {
            long passportLong = Long.parseLong(passportNum);

            // Проверка существования пользователя перед удалением
            String checkSql = "SELECT COUNT(*) FROM users WHERE passport_num = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, passportLong);

            if (count == 0) {
                model.addAttribute("message", "User with passport " + passportNum + " not found");
                return "lab3";
            }

            // Вызов процедуры удаления
            jdbcTemplate.update("CALL delete_user(?)", passportLong);

            model.addAttribute("message", "User deleted successfully!");
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Invalid passport number format");
        } catch (DataAccessException e) {
            model.addAttribute("message", "Error deleting user: " + e.getMessage());
        }
        return "lab3";
    }

}
