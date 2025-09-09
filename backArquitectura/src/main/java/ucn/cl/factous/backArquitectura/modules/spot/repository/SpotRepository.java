package ucn.cl.factous.backArquitectura.modules.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.modules.spot.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}