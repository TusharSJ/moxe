package com.moxe.health.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.moxe.health.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProviderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Provider.class);
        Provider provider1 = new Provider();
        provider1.setId(1L);
        Provider provider2 = new Provider();
        provider2.setId(provider1.getId());
        assertThat(provider1).isEqualTo(provider2);
        provider2.setId(2L);
        assertThat(provider1).isNotEqualTo(provider2);
        provider1.setId(null);
        assertThat(provider1).isNotEqualTo(provider2);
    }
}
