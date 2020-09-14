package vbe;

import java.io.EOFException;
import java.io.IOException;

/**
 * A byte array input using VBE (Variable Byte Encoding).
 */
public class VariableByteArrayInput {

    private byte[] data;
    private int position;
    private int end;

    public VariableByteArrayInput(final byte[] data) {
        this.data = data;
        this.position = 0;
        this.end = data.length;
    }

    public int available() {
        System.out.println("end=" + end + ", position=" + position);  // debug
        return end - position;
    }

    public short readShort() throws IOException {
        if (position == end) {
            throw new EOFException();
        }
        byte b = data[position++];
        short i = (short) (b & 0177);
        for (int shift = 7; (b & 0200) != 0; shift += 7) {
            if (position == end) {
                throw new EOFException();
            }
            b = data[position++];
            i |= (b & 0177) << shift;
        }
        return i;
    }

    public void copyTo(final VariableByteArrayOutput os, final int count) {
        byte more;
        for (int i = 0; i < count; i++) {
            do {
                more = data[position++];
                os.write(more);
            } while ((more & 0x200) > 0);
        }
    }

    public void skip(final int count) {
        for (int i = 0; i < count; i++) {
            while (position < end && (data[position++] & 0200) > 0) {
                //Nothing to do
            }
        }
    }
}