package com.moxe.health.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.moxe.health.IntegrationTest;
import com.moxe.health.domain.Hospital;
import com.moxe.health.domain.Patient;
import com.moxe.health.domain.Provider;
import com.moxe.health.repository.ProviderRepository;
import com.moxe.health.service.criteria.ProviderCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProviderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProviderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALTY = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALTY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProviderMockMvc;

    private Provider provider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createEntity(EntityManager em) {
        Provider provider = new Provider().name(DEFAULT_NAME).specialty(DEFAULT_SPECIALTY);
        return provider;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createUpdatedEntity(EntityManager em) {
        Provider provider = new Provider().name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);
        return provider;
    }

    @BeforeEach
    public void initTest() {
        provider = createEntity(em);
    }

    @Test
    @Transactional
    void createProvider() throws Exception {
        int databaseSizeBeforeCreate = providerRepository.findAll().size();
        // Create the Provider
        restProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isCreated());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeCreate + 1);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProvider.getSpecialty()).isEqualTo(DEFAULT_SPECIALTY);
    }

    @Test
    @Transactional
    void createProviderWithExistingId() throws Exception {
        // Create the Provider with an existing ID
        provider.setId(1L);

        int databaseSizeBeforeCreate = providerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = providerRepository.findAll().size();
        // set the field null
        provider.setName(null);

        // Create the Provider, which fails.

        restProviderMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProviders() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specialty").value(hasItem(DEFAULT_SPECIALTY)));
    }

    @Test
    @Transactional
    void getProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get the provider
        restProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, provider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(provider.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.specialty").value(DEFAULT_SPECIALTY));
    }

    @Test
    @Transactional
    void getProvidersByIdFiltering() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        Long id = provider.getId();

        defaultProviderShouldBeFound("id.equals=" + id);
        defaultProviderShouldNotBeFound("id.notEquals=" + id);

        defaultProviderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProviderShouldNotBeFound("id.greaterThan=" + id);

        defaultProviderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProviderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProvidersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where name equals to DEFAULT_NAME
        defaultProviderShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the providerList where name equals to UPDATED_NAME
        defaultProviderShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvidersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProviderShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the providerList where name equals to UPDATED_NAME
        defaultProviderShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvidersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where name is not null
        defaultProviderShouldBeFound("name.specified=true");

        // Get all the providerList where name is null
        defaultProviderShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProvidersByNameContainsSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where name contains DEFAULT_NAME
        defaultProviderShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the providerList where name contains UPDATED_NAME
        defaultProviderShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvidersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where name does not contain DEFAULT_NAME
        defaultProviderShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the providerList where name does not contain UPDATED_NAME
        defaultProviderShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProvidersBySpecialtyIsEqualToSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where specialty equals to DEFAULT_SPECIALTY
        defaultProviderShouldBeFound("specialty.equals=" + DEFAULT_SPECIALTY);

        // Get all the providerList where specialty equals to UPDATED_SPECIALTY
        defaultProviderShouldNotBeFound("specialty.equals=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllProvidersBySpecialtyIsInShouldWork() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where specialty in DEFAULT_SPECIALTY or UPDATED_SPECIALTY
        defaultProviderShouldBeFound("specialty.in=" + DEFAULT_SPECIALTY + "," + UPDATED_SPECIALTY);

        // Get all the providerList where specialty equals to UPDATED_SPECIALTY
        defaultProviderShouldNotBeFound("specialty.in=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllProvidersBySpecialtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where specialty is not null
        defaultProviderShouldBeFound("specialty.specified=true");

        // Get all the providerList where specialty is null
        defaultProviderShouldNotBeFound("specialty.specified=false");
    }

    @Test
    @Transactional
    void getAllProvidersBySpecialtyContainsSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where specialty contains DEFAULT_SPECIALTY
        defaultProviderShouldBeFound("specialty.contains=" + DEFAULT_SPECIALTY);

        // Get all the providerList where specialty contains UPDATED_SPECIALTY
        defaultProviderShouldNotBeFound("specialty.contains=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllProvidersBySpecialtyNotContainsSomething() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList where specialty does not contain DEFAULT_SPECIALTY
        defaultProviderShouldNotBeFound("specialty.doesNotContain=" + DEFAULT_SPECIALTY);

        // Get all the providerList where specialty does not contain UPDATED_SPECIALTY
        defaultProviderShouldBeFound("specialty.doesNotContain=" + UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void getAllProvidersByPatientIsEqualToSomething() throws Exception {
        Patient patient;
        if (TestUtil.findAll(em, Patient.class).isEmpty()) {
            providerRepository.saveAndFlush(provider);
            patient = PatientResourceIT.createEntity(em);
        } else {
            patient = TestUtil.findAll(em, Patient.class).get(0);
        }
        em.persist(patient);
        em.flush();
        provider.addPatient(patient);
        providerRepository.saveAndFlush(provider);
        Long patientId = patient.getId();

        // Get all the providerList where patient equals to patientId
        defaultProviderShouldBeFound("patientId.equals=" + patientId);

        // Get all the providerList where patient equals to (patientId + 1)
        defaultProviderShouldNotBeFound("patientId.equals=" + (patientId + 1));
    }

    @Test
    @Transactional
    void getAllProvidersByHospitalIsEqualToSomething() throws Exception {
        Hospital hospital;
        if (TestUtil.findAll(em, Hospital.class).isEmpty()) {
            providerRepository.saveAndFlush(provider);
            hospital = HospitalResourceIT.createEntity(em);
        } else {
            hospital = TestUtil.findAll(em, Hospital.class).get(0);
        }
        em.persist(hospital);
        em.flush();
        provider.setHospital(hospital);
        providerRepository.saveAndFlush(provider);
        Long hospitalId = hospital.getId();

        // Get all the providerList where hospital equals to hospitalId
        defaultProviderShouldBeFound("hospitalId.equals=" + hospitalId);

        // Get all the providerList where hospital equals to (hospitalId + 1)
        defaultProviderShouldNotBeFound("hospitalId.equals=" + (hospitalId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProviderShouldBeFound(String filter) throws Exception {
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specialty").value(hasItem(DEFAULT_SPECIALTY)));

        // Check, that the count call also returns 1
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProviderShouldNotBeFound(String filter) throws Exception {
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProvider() throws Exception {
        // Get the provider
        restProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeUpdate = providerRepository.findAll().size();

        // Update the provider
        Provider updatedProvider = providerRepository.findById(provider.getId()).get();
        // Disconnect from session so that the updates on updatedProvider are not directly saved in db
        em.detach(updatedProvider);
        updatedProvider.name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);

        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProvider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProvider.getSpecialty()).isEqualTo(UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void putNonExistingProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, provider.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeUpdate = providerRepository.findAll().size();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        partialUpdatedProvider.name(UPDATED_NAME);

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProvider.getSpecialty()).isEqualTo(DEFAULT_SPECIALTY);
    }

    @Test
    @Transactional
    void fullUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeUpdate = providerRepository.findAll().size();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        partialUpdatedProvider.name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProvider.getSpecialty()).isEqualTo(UPDATED_SPECIALTY);
    }

    @Test
    @Transactional
    void patchNonExistingProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, provider.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();
        provider.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(provider))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeDelete = providerRepository.findAll().size();

        // Delete the provider
        restProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, provider.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
