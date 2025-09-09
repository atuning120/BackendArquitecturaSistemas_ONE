package ucn.cl.factous.backArquitectura.modules.event.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucn.cl.factous.backArquitectura.modules.event.dto.EventDTO;
import ucn.cl.factous.backArquitectura.modules.event.entity.Event;
import ucn.cl.factous.backArquitectura.modules.event.repository.EventRepository;
import ucn.cl.factous.backArquitectura.modules.spot.entity.Spot;
import ucn.cl.factous.backArquitectura.modules.spot.repository.SpotRepository;
import ucn.cl.factous.backArquitectura.modules.user.entity.User;
import ucn.cl.factous.backArquitectura.modules.user.repository.UserRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpotRepository spotRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        return optionalEvent.map(this::convertToDto).orElse(null);
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        User foundedOrganizer = userRepository.findById(eventDTO.getOrganizerId()).orElse(null);
        if (foundedOrganizer == null) {
            throw new IllegalArgumentException("Organizador con ID " + eventDTO.getOrganizerId() + " no existe.");
        }

        Spot foundedSpot = spotRepository.findById(eventDTO.getSpotId()).orElse(null);
        if (foundedSpot == null) {
            throw new IllegalArgumentException("Spot con ID " + eventDTO.getSpotId() + " no existe.");
        }

        Event convertedEvent = new Event(eventDTO.getEventName(), foundedOrganizer, foundedSpot, eventDTO.getEventDate(), eventDTO.getDescription(), eventDTO.getCategory(), eventDTO.getImageUrl(), eventDTO.getTicketPrice(), eventDTO.getCapacity());
        Event savedEvent = eventRepository.save(convertedEvent);
        return convertToDto(savedEvent);
    }

    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event existingEvent = optionalEvent.get();
            existingEvent.setEventName(eventDTO.getEventName());
            existingEvent.setOrganizer(userRepository.findById(eventDTO.getOrganizerId()).orElse(null));
            existingEvent.setSpot(spotRepository.findById(eventDTO.getSpotId()).orElse(null));
            existingEvent.setEventDate(eventDTO.getEventDate());
            existingEvent.setDescription(eventDTO.getDescription());
            existingEvent.setCategory(eventDTO.getCategory());
            existingEvent.setImageUrl(eventDTO.getImageUrl());
            existingEvent.setTicketPrice(eventDTO.getTicketPrice());
            existingEvent.setCapacity(eventDTO.getCapacity());
            Event updatedEvent = eventRepository.save(existingEvent);
            return convertToDto(updatedEvent);
        }
        return null;
    }

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Métodos específicos para diferentes tipos de usuario
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsBySpot(Long spotId) {
        return eventRepository.findBySpotId(spotId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private EventDTO convertToDto(Event event) {
        return new EventDTO(
            event.getId(),
            event.getEventName(),
            event.getDescription(),
            event.getOrganizer().getId(),
            event.getSpot().getId(),
            event.getEventDate(),
            event.getCategory(),
            event.getImageUrl(),
            event.getTicketPrice(),
            event.getCapacity()
        );
    }

    private Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setEventName(eventDTO.getEventName());
        event.setDescription(eventDTO.getDescription());
        event.setSpot(spotRepository.findById(eventDTO.getSpotId()).orElse(null));
        event.setEventDate(eventDTO.getEventDate());
        event.setCategory(eventDTO.getCategory());
        event.setImageUrl(eventDTO.getImageUrl());
        return event;
    }
}