package com.moxe.health.service;

import com.moxe.health.domain.*; // for static metamodels
import com.moxe.health.domain.Provider;
import com.moxe.health.repository.ProviderRepository;
import com.moxe.health.service.criteria.ProviderCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Provider} entities in the database.
 * The main input is a {@link ProviderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Provider} or a {@link Page} of {@link Provider} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProviderQueryService extends QueryService<Provider> {

    private final Logger log = LoggerFactory.getLogger(ProviderQueryService.class);

    private final ProviderRepository providerRepository;

    public ProviderQueryService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    /**
     * Return a {@link List} of {@link Provider} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Provider> findByCriteria(ProviderCriteria criteria) {
        log.debug("find by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Provider> specification = createSpecification(criteria);
        return providerRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Provider} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Provider> findByCriteria(ProviderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria.toString().replaceAll("[\n\r\t]", "_"), page);
        final Specification<Provider> specification = createSpecification(criteria);
        return providerRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProviderCriteria criteria) {
        log.debug("count by criteria : {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        final Specification<Provider> specification = createSpecification(criteria);
        return providerRepository.count(specification);
    }

    /**
     * Function to convert {@link ProviderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Provider> createSpecification(ProviderCriteria criteria) {
        Specification<Provider> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Provider_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Provider_.name));
            }
            if (criteria.getSpecialty() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSpecialty(), Provider_.specialty));
            }
            if (criteria.getPatientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPatientId(), root -> root.join(Provider_.patients, JoinType.LEFT).get(Patient_.id))
                    );
            }
            if (criteria.getHospitalId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getHospitalId(), root -> root.join(Provider_.hospital, JoinType.LEFT).get(Hospital_.id))
                    );
            }
        }
        return specification;
    }
}
