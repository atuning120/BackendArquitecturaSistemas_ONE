package ucn.cl.factous.backArquitectura.modules.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ucn.cl.factous.backArquitectura.modules.ticket.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}