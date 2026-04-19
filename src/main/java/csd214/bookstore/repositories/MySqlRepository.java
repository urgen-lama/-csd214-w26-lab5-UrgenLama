package csd214.bookstore.repositories;

import csd214.bookstore.entities.ProductEntity;

public class MySqlRepository extends JpaRepository {
    public MySqlRepository() {
        super("mysql-pu", "MySQL (Production)");
    }
}