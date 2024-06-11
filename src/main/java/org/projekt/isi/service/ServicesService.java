package org.projekt.isi.service;

import org.projekt.isi.entity.Services;
import org.projekt.isi.repository.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicesService {
    private final ServicesRepository serviceRepository;

    @Autowired
    public ServicesService(ServicesRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    public Services getServiceById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public Services saveService(Services service) {
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
