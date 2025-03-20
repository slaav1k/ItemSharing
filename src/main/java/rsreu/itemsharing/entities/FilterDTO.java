package rsreu.itemsharing.entities;

import java.util.HashMap;
import java.util.Map;

public class FilterDTO {
    private Long category;
    private Map<String, String> filters = new HashMap<>();

    public FilterDTO() {}

    public FilterDTO(Long category, Map<String, String> filters) {
        this.category = category;
        this.filters = filters;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}
