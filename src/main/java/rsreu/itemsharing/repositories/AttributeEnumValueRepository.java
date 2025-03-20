package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.AttributeEnumValue;
import rsreu.itemsharing.entities.AttributeEnumValueId;

import java.util.List;

public interface AttributeEnumValueRepository extends JpaRepository<AttributeEnumValue, AttributeEnumValueId> {
    List<AttributeEnumValue> findById_AttributeId(Long attributeId);
}
