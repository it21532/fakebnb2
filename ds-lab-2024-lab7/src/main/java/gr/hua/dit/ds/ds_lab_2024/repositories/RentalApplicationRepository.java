package gr.hua.dit.ds.ds_lab_2024.repositories;

import gr.hua.dit.ds.ds_lab_2024.entities.RentalApplication;
import gr.hua.dit.ds.ds_lab_2024.entities.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Integer> {
    List<RentalApplication> findByStatus(ApplicationStatus status);

    List<RentalApplication> findByTenantUsernameAndStatus(String tenantUsername, ApplicationStatus status);

    List<RentalApplication> findByPropertyOwnerUsernameAndStatus(String ownerUsername, ApplicationStatus status);
}