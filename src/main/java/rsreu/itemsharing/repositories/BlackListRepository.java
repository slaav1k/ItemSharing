package rsreu.itemsharing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rsreu.itemsharing.entities.BlackList;
import rsreu.itemsharing.entities.BlackListId;
import rsreu.itemsharing.entities.User;

import java.util.List;
import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, BlackListId> {
    Optional<BlackList> findByBlockedUserEntityAndBlockedByUserEntity(User targetUser, User currentUser);

    void deleteByBlockedUserEntityAndBlockedByUserEntity(User targetUser, User currentUser);

    List<BlackList> findByBlockedByUserEntity(User currentUser);

    List<BlackList> findByBlockedUserEntity(User currentUser);
}
