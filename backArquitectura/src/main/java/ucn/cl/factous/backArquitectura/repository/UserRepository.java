package ucn.cl.factous.backArquitectura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}