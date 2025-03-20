package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.CategoryAttribute;
import rsreu.itemsharing.entities.CategoryAttributeId;

import java.util.List;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, CategoryAttributeId> {
    List<CategoryAttribute> findById_CategoryId(Long categoryId);
}
