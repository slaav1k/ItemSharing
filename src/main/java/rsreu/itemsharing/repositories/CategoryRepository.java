package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
