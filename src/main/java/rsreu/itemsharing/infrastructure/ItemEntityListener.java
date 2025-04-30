package rsreu.itemsharing.infrastructure;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.repositories.ItemSearchRepository;

@Component
public class ItemEntityListener {

    private static ItemSearchRepository searchRepository;

    @Autowired
    public void init(ItemSearchRepository repo) {
        searchRepository = repo;
    }

    @PostPersist
    @PostUpdate
    public void indexItem(Item item) {
        ItemDocument doc = new ItemDocument(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAddress()
        );
        searchRepository.save(doc);
    }
}

