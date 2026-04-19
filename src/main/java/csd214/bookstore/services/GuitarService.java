package csd214.bookstore.services;

import csd214.bookstore.entities.ProductEntity;
import csd214.bookstore.repositories.IRepository;
import java.util.List;

public class GuitarService {

    private final IRepository<ProductEntity> repository;

    public GuitarService(IRepository<ProductEntity> repository) {
        this.repository = repository;
    }

    public void applySeasonalTuneUp(Long id) {
        ProductEntity guitar = repository.findById(id);
        if (guitar != null) {
            double currentPrice = guitar.getPrice();
            guitar.setPrice(currentPrice + 75.0);

            String originalName = guitar.getName();
            if (originalName != null && !originalName.contains("(Serviced)")) {
                guitar.setName(originalName + " (Serviced)");
            }

            repository.save(guitar);
        }
    }

    public List<ProductEntity> getAllGuitars() {
        return repository.findAll();
    }

    public ProductEntity getGuitarById(Long id) {
        return repository.findById(id);
    }

    public void addGuitar(ProductEntity guitar) {
        repository.save(guitar);
    }

    public void deleteGuitar(Long id) {
        repository.delete(id);
    }
}