package com.moxe.health.service;

import com.moxe.health.domain.Provider;
import com.moxe.health.repository.ProviderRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Provider}.
 */
@Service
@Transactional
public class ProviderService {

    private final Logger log = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    /**
     * Save a provider.
     *
     * @param provider the entity to save.
     * @return the persisted entity.
     */
    public Provider save(Provider provider) {
        log.debug("Request to save Provider : {}", provider);
        return providerRepository.save(provider);
    }

    /**
     * Update a provider.
     *
     * @param provider the entity to save.
     * @return the persisted entity.
     */
    public Provider update(Provider provider) {
        log.debug("Request to save Provider : {}", provider);
        return providerRepository.save(provider);
    }

    /**
     * Partially update a provider.
     *
     * @param provider the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Provider> partialUpdate(Provider provider) {
        log.debug("Request to partially update Provider : {}", provider);

        return providerRepository
            .findById(provider.getId())
            .map(existingProvider -> {
                if (provider.getName() != null) {
                    existingProvider.setName(provider.getName());
                }
                if (provider.getSpecialty() != null) {
                    existingProvider.setSpecialty(provider.getSpecialty());
                }

                return existingProvider;
            })
            .map(providerRepository::save);
    }

    /**
     * Get all the providers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Provider> findAll(Pageable pageable) {
        log.debug("Request to get all Providers");
        return providerRepository.findAll(pageable);
    }

    /**
     * Get one provider by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Provider> findOne(Long id) {
        log.debug("Request to get Provider : {}", id);
        return providerRepository.findById(id);
    }

    /**
     * Delete the provider by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Provider : {}", id);
        providerRepository.deleteById(id);
    }
}
