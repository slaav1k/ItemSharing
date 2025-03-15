package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.BlackList;
import rsreu.itemsharing.entities.BlackListId;

public interface BlackListRepository extends JpaRepository<BlackList, BlackListId> {
}
