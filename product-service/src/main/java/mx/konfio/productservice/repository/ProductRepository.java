package mx.konfio.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import mx.konfio.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
