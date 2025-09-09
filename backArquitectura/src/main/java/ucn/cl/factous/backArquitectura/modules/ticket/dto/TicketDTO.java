package ucn.cl.factous.backArquitectura.modules.ticket.dto;

public class TicketDTO {
    private Long id;
    private Double price;
    private Long eventId;
    private Long userId;
    private Long saleId;

    public TicketDTO() {}

    public TicketDTO(Double price, Long eventId, Long userId, Long saleId) {
        this.price = price;
        this.eventId = eventId;
        this.userId = userId;
        this.saleId = saleId;
    }

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        if (price != null) {
            this.price = price;
        }
    }

    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        if (eventId != null) {
            this.eventId = eventId;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        if (userId != null) {
            this.userId = userId;
        }
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        if (saleId != null) {
            this.saleId = saleId;
        }
    }
}