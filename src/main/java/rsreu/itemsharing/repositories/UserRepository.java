package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
