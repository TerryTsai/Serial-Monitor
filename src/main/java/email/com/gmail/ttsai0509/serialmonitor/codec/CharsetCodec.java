package email.com.gmail.ttsai0509.serialmonitor.codec;

import java.nio.charset.Charset;

public class CharsetCodec implements Codec {

    private final Charset charset;

    public CharsetCodec(Charset charset) {
        if (charset == null)
            throw new NullPointerException("Charset can not be null.");

        if (!charset.isRegistered())
            throw new RuntimeException("Charset is not registered.");

        this.charset = charset;
    }

    public CharsetCodec(String charset) {
        this(Charset.forName(charset));
    }

    @Override
    public String encode(byte[] dat) {
        return (dat == null) ? NO_STR : new String(dat, charset);
    }

    @Override
    public byte[] decode(String str) {
        return (str == null) ? NO_DAT : str.getBytes(charset);
    }

    @Override
    public String toString() {
        return "CODEC_CHARSET_" + charset.displayName().toUpperCase();
    }
}
