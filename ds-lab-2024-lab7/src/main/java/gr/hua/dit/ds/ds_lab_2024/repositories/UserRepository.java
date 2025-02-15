package gr.hua.dit.ds.ds_lab_2024.repositories;

import gr.hua.dit.ds.ds_lab_2024.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
}