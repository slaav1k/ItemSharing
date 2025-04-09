package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {
}