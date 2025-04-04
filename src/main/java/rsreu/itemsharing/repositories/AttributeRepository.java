package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Attribute;
import rsreu.itemsharing.entities.Category;
import rsreu.itemsharing.entities.CategoryAttributeId;

import java.util.List;
import java.util.stream.DoubleStream;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Attribute findByAttributeId(Long attributeId);
    Attribute findByName(String name);
}
