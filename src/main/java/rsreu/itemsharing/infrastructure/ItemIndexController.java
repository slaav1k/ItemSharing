package rsreu.itemsharing.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin/index")
public class ItemIndexController {

    private final ItemIndexer indexer;

    public ItemIndexController(ItemIndexer indexer) {
        this.indexer = indexer;
    }

    @GetMapping()
    public String showReindexPage() {
        return "index";
    }

    @PostMapping
    public ResponseEntity<String> reindexAll() {
        indexer.reindexAllItems();
        return ResponseEntity.ok("Reindexing started.");
    }
}

