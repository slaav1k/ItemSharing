package rsreu.itemsharing.laba1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired(required = false)
    private DataSource springDataSource;

//    public ItemIndexController(DataSource springDataSource) {
//        this.springDataSource = springDataSource;
//    }

    @GetMapping("/lab1")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("connection");
//        modelAndView.addObject("info", getConnectionInfoByDriverManager());
        modelAndView.addObject("info", getConnectionInfoBySpringDataSource());
        return modelAndView;
    }

    private Map<String, String> getConnectionInfoByDriverManager() {

        String url = "jdbc:postgresql://localhost:5432/ItemSharingBD";
        String user = "postgres";
        String password = "123";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Map<String, String> properties = new LinkedHashMap<>();

            DatabaseMetaData metaData = connection.getMetaData();
            properties.put("URL", metaData.getURL());
            properties.put("Driver name", metaData.getDriverName());
            properties.put("Driver version", metaData.getDriverVersion());

            return properties;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> getConnectionInfoBySpringDataSource() {
        try (Connection connection = springDataSource.getConnection()) {
            Map<String, String> properties = new LinkedHashMap<>();

            DatabaseMetaData metaData = connection.getMetaData();
            properties.put("URL", metaData.getURL());
            properties.put("Driver name", metaData.getDriverName());
            properties.put("Driver version", metaData.getDriverVersion());

            return properties;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}

