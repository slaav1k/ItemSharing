package rsreu.itemsharing.laba2;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import rsreu.itemsharing.laba2.entities.UserLab2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class IndexController2 {

    @GetMapping("/lab2")
    public ModelAndView users() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("statement");
        mav.addObject("users", getUsers());
        return mav;
    }

    private List<UserLab2> getUsers() {
        List<UserLab2> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ItemSharingBD",
                "postgres",
                "123"
        )) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users");

            while (resultSet.next()) {
                long passportNum = resultSet.getLong("passport_num");
                String fullName = resultSet.getString("full_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");

                users.add(new UserLab2(passportNum, fullName, phone, email, address));
            }

        } catch (SQLException e) {
            return Collections.emptyList();
        }
        return users;
    }

    @PostMapping("/lab2/findUsers")
    public String findUser(@RequestParam String phone, Model model) {
        model.addAttribute("findedUsers", findUsers(phone));
        return "statement";
    }

    private List<UserLab2> findUsers(String phoneToSearch) {
        List<UserLab2> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ItemSharingBD",
                "postgres",
                "123"
        )) {
            PreparedStatement statement = connection.prepareStatement("select * from users where phone = ?");
            statement.setString(1, phoneToSearch);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long passportNum = resultSet.getLong("passport_num");
                String fullName = resultSet.getString("full_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");

                users.add(new UserLab2(passportNum, fullName, phone, email, address));
            }

        } catch (SQLException e) {
            return Collections.emptyList();
        }
        return users;

    }

    @PostMapping("/lab2/findSmthUsers")
    public String findSmthUsers(@RequestParam String sql, Model model) {
        model.addAttribute("findedSmthUsers", findSmthUsers(sql));
        return "statement";
    }

    private List<UserLab2> findSmthUsers(String sql) {
        List<UserLab2> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ItemSharingBD",
                "postgres",
                "123"
        )) {
            Statement statement = connection.createStatement();
            System.out.println("Executing SQL: " + sql);
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                long passportNum = resultSet.getLong("passport_num");
                String fullName = resultSet.getString("full_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");

                users.add(new UserLab2(passportNum, fullName, phone, email, address));
            }

        } catch (SQLException e) {
            return Collections.emptyList();
        }
        return users;
    }

    @PostMapping("/lab2/findSmthUsersByNameByCity")
    public String findSmthUsersByNameByCity(@RequestParam String fio, String city, Model model) {
        model.addAttribute("findedSmthUsersByNameByCity", findSmthUsersByNameByCity(fio, city));
        return "statement";
    }

    private List<UserLab2> findSmthUsersByNameByCity(String FIO, String city) {
        List<UserLab2> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ItemSharingBD",
                "postgres",
                "123"
        )) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users \n" +
                                                            "WHERE full_name ILIKE COALESCE(?, full_name) \n" +
                                                            "AND address ILIKE COALESCE(?, address)");

            statement.setString(1, FIO != null ? "%" + FIO + "%" : null);
            statement.setString(2, city != null ? "%" + city + "%" : null);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long passportNum = resultSet.getLong("passport_num");
                String fullName = resultSet.getString("full_name");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");

                users.add(new UserLab2(passportNum, fullName, phone, email, address));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return users;

    }


}
