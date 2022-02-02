package com.example.space.repos;

import com.example.space.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Override
    void deleteById(Long aLong);
    User findByEmail(String email);
    User findByActivationCode(String code);
}
