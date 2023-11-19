package com.example.catalogservice.respository;

import com.example.catalogservice.entity.CatalogEntity;
import org.springframework.data.repository.CrudRepository;

public interface CatalogRepository extends CrudRepository<CatalogEntity, Long> {

    CatalogEntity findByProductId(String productId);

}
