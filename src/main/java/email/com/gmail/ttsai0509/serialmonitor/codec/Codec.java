package email.com.gmail.ttsai0509.serialmonitor.codec;


/******************************************************************
 *                                                                *
 * An encoding/decoding scheme between String and byte[] data.
 *
 * Exceptions are NOT propagated. Instead, the codec should
 * return NO_DAT and NO_STR for any exceptions.
 *                                                                *
 ******************************************************************/

public interface Codec {

    static byte[] NO_DAT = new byte[0];

    static String NO_STR = "";

    String encode(byte[] dat);

    byte[] decode(String str);

}
