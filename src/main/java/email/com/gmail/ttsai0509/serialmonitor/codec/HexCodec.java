package email.com.gmail.ttsai0509.serialmonitor.codec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexCodec implements Codec {

    @Override
    public String encode(byte[] dat) {
        return (dat == null) ? NO_STR : new String(Hex.encodeHex(dat));
    }

    @Override
    public byte[] decode(String str) {
        try {
            return (str == null) ? NO_DAT : Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            return NO_DAT;
        }
    }

    @Override
    public String toString() {
        return "CODEC_HEX";
    }
}
