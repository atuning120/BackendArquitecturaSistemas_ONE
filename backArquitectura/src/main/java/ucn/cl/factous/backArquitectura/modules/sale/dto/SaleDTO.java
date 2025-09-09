package ucn.cl.factous.backArquitectura.modules.sale.dto;

import java.util.Date;
import java.util.List;

import ucn.cl.factous.backArquitectura.modules.sale.entity.Sale;
import ucn.cl.factous.backArquitectura.modules.ticket.entity.Ticket;

public class SaleDTO {
    private Long id;
    private List<Long> ticketsIDs;
    private Long buyerId;
    private Date saleDate;
    private Double amount;

    public SaleDTO() {}

    public SaleDTO(Long id, Long buyerId, Date saleDate, Double amount) {
        this.id = id;
        this.buyerId = buyerId;
        this.saleDate = saleDate;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        if (buyerId != null) {
            this.buyerId = buyerId;
        }
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        if (saleDate != null) {
            this.saleDate = saleDate;
        }
    }

    public Double getAmount() {
        return amount;
    }   

    public void setAmount(Double amount) {
        if (amount != null) {
            this.amount = amount;
        }
    }

    public List<Long> getTicketsIDs() {
        return ticketsIDs;
    }
}