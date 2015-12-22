package email.com.gmail.ttsai0509.serialmonitor.codec;

public class PlatformCodec implements Codec {

    @Override
    public String encode(byte[] dat) {
        return (dat == null) ? NO_STR : new String(dat);
    }

    @Override
    public byte[] decode(String str) {
        return (str == null) ? NO_DAT : str.getBytes();
    }

    @Override
    public String toString() {
        return "CODEC_PLATFORM";
    }
}