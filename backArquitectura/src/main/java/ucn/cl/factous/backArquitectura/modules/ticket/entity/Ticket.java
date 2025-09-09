package ucn.cl.factous.backArquitectura.modules.ticket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ucn.cl.factous.backArquitectura.modules.user.entity.User;
import ucn.cl.factous.backArquitectura.modules.event.entity.Event;
import ucn.cl.factous.backArquitectura.modules.sale.entity.Sale;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    public Ticket() {}

    public Ticket(Double price, Event event, User user, Sale sale) {
        this.price = price;
        this.event = event;
        this.user = user;
        this.sale = sale;
    }

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }   

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
    
    @Override
    public String toString() {
        return "Ticket [id=" + id + ", price=" + price + ", event=" + event.toString() + ", user=" + user.toString() + "]";
    }
}
