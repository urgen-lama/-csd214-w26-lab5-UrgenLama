package csd214.bookstore;

import com.github.javafaker.Faker;
import csd214.bookstore.entities.*;
import csd214.bookstore.pojos.*;
import csd214.bookstore.repositories.IRepository;
import csd214.bookstore.services.BookstoreService;
import csd214.bookstore.services.GuitarService;

import java.util.List;
import java.util.Scanner;

public class App {
    private IRepository<ProductEntity> repository;
    private BookstoreService service;
    private GuitarService guitarService;
    private CashTill cashTill = new CashTill();
    private Scanner input = new Scanner(System.in);

    public App(IRepository<ProductEntity> repository, GuitarService guitarService, BookstoreService bookstoreService) {
        this.repository = repository;
        this.guitarService = guitarService;
        this.service = bookstoreService;
    }

    public void run() {
        System.out.println("Running on: " + repository.getDataSourceType());
        if (repository.count() == 0) {
            populate();
        }

        int choice = 0;
        while (choice != 99) {
            System.out.println("\n***********************");
            System.out.println("      JPA STORE        ");
            System.out.println("***********************");
            System.out.println(" 1. Add Items");
            System.out.println(" 2. Edit Items");
            System.out.println(" 3. Delete Items");
            System.out.println(" 4. Sell item(s)");
            System.out.println(" 5. List items");
            System.out.println(" 6. Apply Guitar Tune-Up");
            System.out.println(" 7. System Reset (Wipe DB)");
            System.out.println("99. Quit");
            System.out.println("***********************");
            System.out.print("Enter choice: ");

            try {
                String line = input.nextLine();
                if (line.trim().isEmpty()) continue;
                choice = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                choice = 0;
            }

            switch (choice) {
                case 1: addItem(); break;
                case 2: editItem(); break;
                case 3: deleteItem(); break;
                case 4: sellItem(); break;
                case 5: listAny(); break;
                case 6: applyTuneUp(); break;
                case 7: systemReset(); break;
                case 99: System.out.println("Goodbye."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    public void addItem() {
        System.out.println("\n--- Add an item ---");
        System.out.println("1. Book");
        System.out.println("2. Magazine");
        System.out.println("3. Ticket");
        System.out.println("4. Pen");
        System.out.println("99. Back");

        int choice = getIntInput();
        if (choice == 99) return;

        try {
            switch(choice) {
                case 1:
                    Book bPojo = new Book();
                    bPojo.initialize(input);
                    BookEntity bEnt = new BookEntity();
                    bEnt.setTitle(bPojo.getTitle());
                    bEnt.setPrice(bPojo.getPrice());
                    bEnt.setCopies(bPojo.getCopies());
                    bEnt.setAuthor(bPojo.getAuthor());
                    repository.save(bEnt);
                    break;
                case 4:
                    Pen pPojo = new Pen();
                    pPojo.initialize(input);
                    PenEntity pEnt = new PenEntity();
                    pEnt.setBrand(pPojo.getBrand());
                    pEnt.setPrice(pPojo.getPrice());
                    pEnt.setColor(pPojo.getColor());
                    pEnt.setName(pPojo.getColor() + " " + pPojo.getBrand() + " Pen");
                    repository.save(pEnt);
                    break;
                default:
                    System.out.println("Invalid type.");
            }
            System.out.println("Item saved!");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    public void listAny() {
        List<ProductEntity> results = repository.findAll();
        System.out.println("\n--- Inventory List (" + results.size() + ") ---");
        for (int i = 0; i < results.size(); i++) {
            System.out.println("[" + i + "] " + results.get(i));
        }
    }

    public void editItem() {
        List<ProductEntity> results = repository.findAll();
        if (results.isEmpty()) return;
        listAny();
        System.out.print("Select index to edit: ");
        int idx = getIntInput();
        if (idx < 0 || idx >= results.size()) return;

        ProductEntity entity = results.get(idx);
        try {
            if (entity instanceof BookEntity) {
                BookEntity be = (BookEntity) entity;
                Book pojo = new Book(be.getAuthor(), be.getTitle(), be.getPrice(), be.getCopies());
                pojo.edit(input);
                be.setAuthor(pojo.getAuthor());
                be.setTitle(pojo.getTitle());
                be.setPrice(pojo.getPrice());
                be.setCopies(pojo.getCopies());
            }
            repository.save(entity);
            System.out.println("Update successful.");
        } catch (Exception e) {
            System.out.println("Error updating.");
        }
    }

    public void deleteItem() {
        List<ProductEntity> results = repository.findAll();
        listAny();
        System.out.print("Select index to delete: ");
        int idx = getIntInput();
        if (idx < 0 || idx >= results.size()) return;
        repository.delete(results.get(idx).getId());
        System.out.println("Deleted.");
    }

    public void sellItem() {
        List<ProductEntity> results = repository.findAll();
        if (results.isEmpty()) return;
        listAny();
        System.out.print("Select index to sell: ");
        int idx = getIntInput();
        if (idx < 0 || idx >= results.size()) return;

        ProductEntity item = results.get(idx);
        service.performSale(item.getId());
        cashTill.sellItem(new SaleableItem() {
            @Override
            public void sellItem() {}
            @Override
            public double getPrice() { return item.getPrice(); }
        });
        System.out.println("Transaction complete.");
    }

    public void applyTuneUp() {
        List<ProductEntity> results = repository.findAll();
        if (results.isEmpty()) return;
        listAny();
        System.out.print("Select guitar index for tune-up: ");
        int idx = getIntInput();
        if (idx >= 0 && idx < results.size()) {
            guitarService.applySeasonalTuneUp(results.get(idx).getId());
            System.out.println("Tune-up complete. Price updated.");
        }
    }

    public void systemReset() {
        int deletedCount = repository.deleteAll();
        System.out.println("Reset complete. " + deletedCount + " items removed.");
    }

    public void populate() {
        Faker faker = new Faker();
        for (int i = 0; i < 3; i++) {
            BookEntity b = new BookEntity();
            b.setAuthor(faker.book().author());
            b.setTitle(faker.book().title());
            b.setPrice(faker.number().randomDouble(2, 10, 50));
            b.setCopies(faker.number().numberBetween(1, 20));
            repository.save(b);
        }
    }

    private int getIntInput() {
        try {
            String line = input.nextLine();
            if (line.trim().isEmpty()) return -1;
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}