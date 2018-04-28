package by.mrj.message.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static by.mrj.crypto.util.CryptoUtils.doubleSha256;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Builder
@EqualsAndHashCode
public class Registration implements Serializable, Hashable {
//    @NonNull
    String networkAddress; // host
//    @NonNull
    String address; // public key hash sha256ripemd160

    @Override
    public String hash() {
        return doubleSha256(networkAddress + address);
    }
}
