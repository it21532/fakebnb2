package gr.hua.dit.ds.ds_lab_2024.service;

import gr.hua.dit.ds.ds_lab_2024.entities.Owner;
import gr.hua.dit.ds.ds_lab_2024.repositories.propertyRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.ownerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    private final ownerRepository ownerRepository;

    public OwnerService(ownerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }
    public Owner getOwnerByUsername(String username) {
        return ownerRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }
}
