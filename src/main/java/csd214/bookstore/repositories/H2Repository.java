package csd214.bookstore.repositories;

import csd214.bookstore.entities.ProductEntity;

public class H2Repository extends JpaRepository {
    public H2Repository() {
        super("h2-pu", "H2 (In-Memory SQL)");
    }
}