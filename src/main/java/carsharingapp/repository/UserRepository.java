package carsharingapp.repository;

import carsharingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u LEFT JOIN FETCH u.roles r "
    + "WHERE u.email =:email "
    + "AND u.isDeleted = FALSE "
    + "AND r.isDeleted = FALSE")
    Optional<User> findByEmail(String email);
}
