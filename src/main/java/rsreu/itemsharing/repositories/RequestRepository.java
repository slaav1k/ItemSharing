package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Item;
import rsreu.itemsharing.entities.Request;
import rsreu.itemsharing.entities.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByItem(Item item);

    List<Request> findByHolder(User user);
    List<Request> findByItemOwner(User user);
}
