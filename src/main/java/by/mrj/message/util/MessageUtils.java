package by.mrj.message.util;

import by.mrj.crypto.util.CryptoUtils;
import by.mrj.crypto.util.EncodingUtils;
import by.mrj.message.domain.Hashable;
import by.mrj.message.domain.Message;
import by.mrj.message.types.Command;

import java.io.Serializable;
import java.util.Objects;
import com.google.common.io.BaseEncoding;

import static by.mrj.crypto.util.CryptoUtils.doubleSha256;
import static by.mrj.crypto.util.CryptoUtils.privateKey;

public abstract class MessageUtils {
    // TODO
    public static <T extends Hashable & Serializable> Message<T> makeMessage(T payload, Command command) {
        return Message.<T>builder()
                .payload(payload)
                .command(command)
                .checksum(doubleSha256(payload.hash() + command))
                .build();
    }

    public static <T extends Hashable & Serializable> Message<T> makeMessageWithPubKey(T payload, Command command) {
        String publicKey = BaseEncoding.base16().encode(CryptoUtils.pubKey);
        String address = CryptoUtils.sha256ripemd160(publicKey);
        String checksum = doubleSha256(payload.hash() + address + publicKey + command); // msg hash
        return Message.<T>builder()
                .payload(payload)
                .command(command)
                .address(address)
                .publicKey(publicKey)
                .checksum(checksum)
                .build();
    }

    public static <T extends Hashable & Serializable> Message<T> makeMessageWithSig(T payload, Command command) {
        Message<T> message = makeMessageWithPubKey(payload, command);
        signMessage(message);
        return message;
    }

    public static void signMessage(Message<?> msg) {
        byte[] signature = CryptoUtils.sign(msg.getChecksum().getBytes(), privateKey);
        msg.setSignature(EncodingUtils.HEX.encode(signature));
    }

    public static boolean verifyMessage(Message<?> msg) {
        String checksum = msg.getChecksum();
        if (checksum == null || !Objects.equals(checksum, msg.hash())) {
            return false;
        }

        byte[] signature = EncodingUtils.HEX.decode(msg.getSignature());
        byte[] pubKey = EncodingUtils.HEX.decode(msg.getPublicKey());

        return CryptoUtils.verifySignature(checksum.getBytes(), pubKey, signature);
    }
}
