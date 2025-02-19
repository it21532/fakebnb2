package gr.hua.dit.ds.ds_lab_2024.repositories;

import gr.hua.dit.ds.ds_lab_2024.entities.PropertyStatus;
import gr.hua.dit.ds.ds_lab_2024.entities.property;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface propertyRepository extends JpaRepository<property, String> {
    List<property> findByStatus(PropertyStatus status);
    List<property> findByStatus(PropertyStatus status, Sort sort);
    List<property> findByPriceBetweenAndStatus(Integer minPrice, Integer maxPrice, PropertyStatus status);
    List<property> findBySquareMetersBetweenAndStatus(Integer minSquareMeters, Integer maxSquareMeters, PropertyStatus status);
    List<property> findByOwnerUsername(String ownerUsername);
}
