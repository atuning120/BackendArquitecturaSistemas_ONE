package ucn.cl.factous.backArquitectura.modules.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ucn.cl.factous.backArquitectura.modules.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findBySpotId(Long spotId);
    String findTitleById(Long eventId);
}