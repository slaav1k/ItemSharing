package rsreu.itemsharing.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.entities.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller("/")
public class MainWindowController {

    @GetMapping()
    public String catalog(@RequestParam(required = false) Long category, Model model) {
        model.addAttribute("items", getItems());
        return "catalog";
    }

    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ItemSharingBD",
                "postgres",
                "123"
        )) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from item");

            while (resultSet.next()) {
                String itemId = resultSet.getString("item_id");
                long owner = resultSet.getLong("owner");
                String name = resultSet.getString("name");
                long category = resultSet.getLong("category");
                String description = resultSet.getString("description");
                String address = resultSet.getString("address");
                boolean isAvailable = resultSet.getBoolean("is_available");
                String sizes = resultSet.getString("sizes");
                double weight = resultSet.getDouble("weight");
                String color = resultSet.getString("color");
                String material = resultSet.getString("material");
                String maker = resultSet.getString("maker");
                String model = resultSet.getString("model");
                long releaseYear = resultSet.getLong("release_year");
                items.add(new Item(itemId, owner, name, category, description, address, isAvailable, sizes, weight, color, material, maker, model, releaseYear));
            }


        } catch (SQLException e) {
            return Collections.emptyList();
        }
        return items;
    }
}
