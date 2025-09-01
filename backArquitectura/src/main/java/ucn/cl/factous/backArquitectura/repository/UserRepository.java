package ucn.cl.factous.backArquitectura.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}