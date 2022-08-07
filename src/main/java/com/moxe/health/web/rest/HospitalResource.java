package com.moxe.health.web.rest;

import com.moxe.health.domain.Hospital;
import com.moxe.health.repository.HospitalRepository;
import com.moxe.health.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.moxe.health.domain.Hospital}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HospitalResource {

    private final Logger log = LoggerFactory.getLogger(HospitalResource.class);

    private static final String ENTITY_NAME = "hospital";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HospitalRepository hospitalRepository;

    public HospitalResource(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * {@code POST  /hospitals} : Create a new hospital.
     *
     * @param hospital the hospital to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hospital, or with status {@code 400 (Bad Request)} if the hospital has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/hospitals")
    public ResponseEntity<Hospital> createHospital(@Valid @RequestBody Hospital hospital) throws URISyntaxException {
        log.debug("REST request to save Hospital : {}", hospital);
        if (hospital.getId() != null) {
            throw new BadRequestAlertException("A new hospital cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Hospital result = hospitalRepository.save(hospital);
        return ResponseEntity
            .created(new URI("/api/hospitals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /hospitals/:id} : Updates an existing hospital.
     *
     * @param id the id of the hospital to save.
     * @param hospital the hospital to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hospital,
     * or with status {@code 400 (Bad Request)} if the hospital is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hospital couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/hospitals/{id}")
    public ResponseEntity<Hospital> updateHospital(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Hospital hospital
    ) throws URISyntaxException {
        log.debug("REST request to update Hospital : {}, {}", id, hospital);
        if (hospital.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hospital.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hospitalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Hospital result = hospitalRepository.save(hospital);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, hospital.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /hospitals/:id} : Partial updates given fields of an existing hospital, field will ignore if it is null
     *
     * @param id the id of the hospital to save.
     * @param hospital the hospital to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hospital,
     * or with status {@code 400 (Bad Request)} if the hospital is not valid,
     * or with status {@code 404 (Not Found)} if the hospital is not found,
     * or with status {@code 500 (Internal Server Error)} if the hospital couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/hospitals/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Hospital> partialUpdateHospital(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Hospital hospital
    ) throws URISyntaxException {
        log.debug("REST request to partial update Hospital partially : {}, {}", id, hospital);
        if (hospital.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hospital.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hospitalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Hospital> result = hospitalRepository
            .findById(hospital.getId())
            .map(existingHospital -> {
                if (hospital.getName() != null) {
                    existingHospital.setName(hospital.getName());
                }
                if (hospital.getDescription() != null) {
                    existingHospital.setDescription(hospital.getDescription());
                }

                return existingHospital;
            })
            .map(hospitalRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, hospital.getId().toString())
        );
    }

    /**
     * {@code GET  /hospitals} : get all the hospitals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hospitals in body.
     */
    @GetMapping("/hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Hospitals");
        Page<Hospital> page = hospitalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /hospitals/:id} : get the "id" hospital.
     *
     * @param id the id of the hospital to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hospital, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/hospitals/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        log.debug("REST request to get Hospital : {}", id);
        Optional<Hospital> hospital = hospitalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hospital);
    }

    /**
     * {@code DELETE  /hospitals/:id} : delete the "id" hospital.
     *
     * @param id the id of the hospital to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/hospitals/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        log.debug("REST request to delete Hospital : {}", id);
        hospitalRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
