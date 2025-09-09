package ucn.cl.factous.backArquitectura.modules.event.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.modules.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}