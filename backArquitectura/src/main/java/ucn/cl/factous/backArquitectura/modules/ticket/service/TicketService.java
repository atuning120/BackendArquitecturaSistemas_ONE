package ucn.cl.factous.backArquitectura.modules.ticket.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucn.cl.factous.backArquitectura.modules.event.entity.Event;
import ucn.cl.factous.backArquitectura.modules.event.repository.EventRepository;
import ucn.cl.factous.backArquitectura.modules.sale.entity.Sale;
import ucn.cl.factous.backArquitectura.modules.sale.repository.SaleRepository;
import ucn.cl.factous.backArquitectura.modules.ticket.dto.TicketDTO;
import ucn.cl.factous.backArquitectura.modules.ticket.entity.Ticket;
import ucn.cl.factous.backArquitectura.modules.ticket.repository.TicketRepository;
import ucn.cl.factous.backArquitectura.modules.user.entity.User;
import ucn.cl.factous.backArquitectura.modules.user.repository.UserRepository;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SaleRepository saleRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Long id) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        return ticketOptional.map(this::convertToDto).orElse(null);
    }

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        User buyerFound = userRepository.findById(ticketDTO.getUserId()).orElse(null);
        if (buyerFound == null) {
            throw new IllegalArgumentException("Comprador con ID " + ticketDTO.getUserId() + " no existe.");
        }

        Event eventFound = eventRepository.findById(ticketDTO.getEventId()).orElse(null);
        if (eventFound == null) {
            throw new IllegalArgumentException("Evento con ID " + ticketDTO.getEventId() + " no existe.");
        }

        Sale saleFound = saleRepository.findById(ticketDTO.getSaleId()).orElse(null);
        if (saleFound == null) {
            throw new IllegalArgumentException("Venta con ID " + ticketDTO.getSaleId() + " no existe.");
        }

        Ticket convertedTicket = new Ticket(ticketDTO.getPrice(), eventFound, buyerFound, saleFound);
        Ticket savedTicket = ticketRepository.save(convertedTicket);
        return convertToDto(savedTicket);
    }

    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket existingTicket = optionalTicket.get();
            existingTicket.setPrice(ticketDTO.getPrice());
            existingTicket.setEvent(eventRepository.findById(ticketDTO.getEventId()).orElse(null));
            existingTicket.setUser(userRepository.findById(ticketDTO.getUserId()).orElse(null));
            existingTicket.setSale(saleRepository.findById(ticketDTO.getSaleId()).orElse(null));
            Ticket updatedTicket = ticketRepository.save(existingTicket);
            return convertToDto(updatedTicket);
        }
        return null;
    }

    public boolean deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TicketDTO convertToDto(Ticket ticket) {
        return new TicketDTO(ticket.getPrice(), ticket.getEvent().getId(), ticket.getUser().getId(), ticket.getSale().getId());
    }

    private Ticket convertToEntity(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        ticket.setPrice(ticketDTO.getPrice());
        ticket.setEvent(eventRepository.findById(ticketDTO.getEventId()).orElse(null));
        ticket.setUser(userRepository.findById(ticketDTO.getUserId()).orElse(null));
        ticket.setSale(saleRepository.findById(ticketDTO.getSaleId()).orElse(null));
        return ticket;
    }
}