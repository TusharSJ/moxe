package com.moxe.health.service.criteria;

import com.moxe.health.domain.enumeration.Sex;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.moxe.health.domain.Patient} entity. This class is used
 * in {@link com.moxe.health.web.rest.PatientResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /patients?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PatientCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Sex
     */
    public static class SexFilter extends Filter<Sex> {

        public SexFilter() {}

        public SexFilter(SexFilter filter) {
            super(filter);
        }

        @Override
        public SexFilter copy() {
            return new SexFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private InstantFilter dateOfBirth;

    private SexFilter sex;

    private LongFilter providerId;

    private Boolean distinct;

    public PatientCriteria() {}

    public PatientCriteria(PatientCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.dateOfBirth = other.dateOfBirth == null ? null : other.dateOfBirth.copy();
        this.sex = other.sex == null ? null : other.sex.copy();
        this.providerId = other.providerId == null ? null : other.providerId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PatientCriteria copy() {
        return new PatientCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public InstantFilter getDateOfBirth() {
        return dateOfBirth;
    }

    public InstantFilter dateOfBirth() {
        if (dateOfBirth == null) {
            dateOfBirth = new InstantFilter();
        }
        return dateOfBirth;
    }

    public void setDateOfBirth(InstantFilter dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public SexFilter getSex() {
        return sex;
    }

    public SexFilter sex() {
        if (sex == null) {
            sex = new SexFilter();
        }
        return sex;
    }

    public void setSex(SexFilter sex) {
        this.sex = sex;
    }

    public LongFilter getProviderId() {
        return providerId;
    }

    public LongFilter providerId() {
        if (providerId == null) {
            providerId = new LongFilter();
        }
        return providerId;
    }

    public void setProviderId(LongFilter providerId) {
        this.providerId = providerId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PatientCriteria that = (PatientCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(dateOfBirth, that.dateOfBirth) &&
            Objects.equals(sex, that.sex) &&
            Objects.equals(providerId, that.providerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfBirth, sex, providerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatientCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (dateOfBirth != null ? "dateOfBirth=" + dateOfBirth + ", " : "") +
            (sex != null ? "sex=" + sex + ", " : "") +
            (providerId != null ? "providerId=" + providerId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
