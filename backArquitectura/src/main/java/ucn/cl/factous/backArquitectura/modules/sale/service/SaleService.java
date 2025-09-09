package ucn.cl.factous.backArquitectura.modules.sale.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ucn.cl.factous.backArquitectura.modules.sale.dto.SaleDTO;
import ucn.cl.factous.backArquitectura.modules.sale.entity.Sale;
import ucn.cl.factous.backArquitectura.modules.sale.repository.SaleRepository;
import ucn.cl.factous.backArquitectura.modules.user.entity.User;
import ucn.cl.factous.backArquitectura.modules.user.repository.UserRepository;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private UserRepository userRepository;

    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SaleDTO getSaleById(Long id) {
        Optional<Sale> saleOptional = saleRepository.findById(id);
        return saleOptional.map(this::convertToDto).orElse(null);
    }

    public SaleDTO createSale(SaleDTO saleDTO) {
        User buyerFound = userRepository.findById(saleDTO.getBuyerId()).orElse(null);
        if (buyerFound == null) {
            throw new IllegalArgumentException("Comprador con ID " + saleDTO.getBuyerId() + " no existe.");
        }
        Sale convertedSale = new Sale(buyerFound, saleDTO.getSaleDate(), saleDTO.getAmount());
        Sale savedSale = saleRepository.save(convertedSale);
        return convertToDto(savedSale);
    }

    public SaleDTO updateSale(Long id, SaleDTO saleDTO) {
        Optional<Sale> optionalSale = saleRepository.findById(id);
        if (optionalSale.isPresent()) {
            Sale existingSale = optionalSale.get();
            existingSale.setBuyer(userRepository.findById(saleDTO.getBuyerId()).orElse(null));
            existingSale.setSaleDate(saleDTO.getSaleDate());
            existingSale.setAmount(saleDTO.getAmount());
            Sale updatedSale = saleRepository.save(existingSale);
            return convertToDto(updatedSale);
        }
        return null;
    }

    public boolean deleteSale(Long id) {
        if (saleRepository.existsById(id)) {
            saleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private SaleDTO convertToDto(Sale sale) {
        return new SaleDTO(sale.getId(), sale.getBuyer().getId(), sale.getSaleDate(), sale.getAmount());
    }

    private Sale convertToEntity(SaleDTO saleDTO) {
        Sale sale = new Sale();
        sale.setBuyer(userRepository.findById(saleDTO.getBuyerId()).orElse(null));
        sale.setSaleDate(saleDTO.getSaleDate());
        sale.setAmount(saleDTO.getAmount());
        return sale;
    }
}