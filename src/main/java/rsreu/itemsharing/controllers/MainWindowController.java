package rsreu.itemsharing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rsreu.itemsharing.infrastructure.ItemSearchService;
import rsreu.itemsharing.security.CustomUserDetails;
import rsreu.itemsharing.infrastructure.ItemDocument;
import rsreu.itemsharing.services.CatalogService;
import rsreu.itemsharing.services.CityService;

import java.util.List;
import java.util.Map;

@Controller("/")
public class MainWindowController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CityService cityService;

    @Autowired
    private ItemSearchService itemSearchService;

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDocument> search(@RequestParam String q, @RequestParam(required = false) String city) {
        System.out.println("Search query received: q=" + q + ", city=" + (city == null ? "null" : "'" + city + "'"));
        List<ItemDocument> results = itemSearchService.search(q, city);
        System.out.println("Search results: " + results.size() + " items");
        return results;
    }

    @GetMapping
    public String catalog(@RequestParam(required = false) Long category,
                          @RequestParam(required = false) String search,
                          @RequestParam(required = false) String searchIds,
                          @RequestParam(required = false) String city,
                          @RequestParam(required = false) Map<String, String> filters,
                          Model model) {
        // Получаем текущего пользователя
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = principal instanceof CustomUserDetails ? (CustomUserDetails) principal : null;

        // Получаем данные из сервиса
        Map<String, Object> modelAttributes = catalogService.getCatalogData(category, search, searchIds, city, filters, userDetails);

        // Добавляем города из CityService
        modelAttributes.put("cities", cityService.getCities());

        // Передаём данные в модель
        modelAttributes.forEach(model::addAttribute);

        return "catalog";
    }
}