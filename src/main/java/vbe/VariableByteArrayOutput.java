package vbe;

import java.io.ByteArrayOutputStream;

/**
 * A byte array output using VBE (Variable Byte Encoding).
 */
public class VariableByteArrayOutput {
    private ByteArrayOutputStream buf;

    public VariableByteArrayOutput() {
        buf = new ByteArrayOutputStream();
    }

    public byte[] toByteArray() {
        return buf.toByteArray();
    }

    public void write(final int b) {
        buf.write((byte) b);
    }

    /**
     * Writes a VBE short to the output stream
     *
     * The encoding scheme requires the following storage
     * for numbers between (inclusive):
     *
     *  {@link Short#MIN_VALUE} and -1, 5 bytes
     *  0 and 127, 1 byte
     *  128 and 16383, 2 bytes
     *  16384 and {@link Short#MAX_VALUE}, 3 bytes
     *
     *  @param s the short to write
     */
    public void writeShort(int s) {
        while ((s & ~0177) != 0) {
            buf.write((byte) ((s & 0177) | 0200));
            s >>>= 7;
        }
        buf.write((byte) s);
    }
}
