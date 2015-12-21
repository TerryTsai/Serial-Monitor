package email.com.gmail.ttsai0509.serialmonitor.codec;

public class MixedCodec implements Codec {

    private final static DefaultCodec defCodec = new DefaultCodec();
    private final static BinaryCodec binCodec = new BinaryCodec();
    private final static HexCodec hexCodec = new HexCodec();

    private final String binPrefix;
    private final String hexPrefix;

    public MixedCodec(String binPrefix, String hexPrefix) {
        this.binPrefix = binPrefix;
        this.hexPrefix = hexPrefix;
    }

    @Override
    public String encode(byte[] dat) {
        return null;
    }

    @Override
    public byte[] decode(String str) {
        return new byte[0];
    }
}
