package ucn.cl.factous.backArquitectura.shared.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.shared.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByEventId(Long eventId);
}
