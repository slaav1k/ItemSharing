package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.Color;

public interface ColorRepository extends JpaRepository<Color, Long> {
}