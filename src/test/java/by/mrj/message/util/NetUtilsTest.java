package by.mrj.message.util;

import by.mrj.message.domain.Registration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NetUtilsTest {

    @Test
    public void serialize() {
        Registration registration = Registration.builder()
                .address("123")
                .networkAddress("123123")
                .build();

        byte[] bytes = NetUtils.serialize(registration, false);

        Registration o = NetUtils.deserialize(bytes, Registration.class);

        assertThat(registration).isEqualTo(o);
    }
}