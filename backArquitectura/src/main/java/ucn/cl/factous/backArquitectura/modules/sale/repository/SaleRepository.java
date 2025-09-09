package ucn.cl.factous.backArquitectura.modules.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.modules.sale.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}