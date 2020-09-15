package vbe;

import java.io.IOException;

public class VbeMain {

    public static void main(final String args[]) {
        // perform 10 iterations
        for (int i = 0; i < 10; i++) {

            try {
                final IntTuple result = simplifiedTest();
                if (result.expected != result.actual) {
                    System.err.println("Error: "+ result.expected + " != " + result.actual);
                }
            } catch (final IOException x) {
                x.printStackTrace();
            }

        }
    }

    public static IntTuple simplifiedTest() throws IOException {
        final short[][] values = new short[22][];
        values[0] = new short[2389];
        values[1] = new short[1351];
        values[2] = new short[4264];
        values[3] = new short[5357];
        values[4] = new short[12962];
        values[5] = new short[13167];
        values[6] = new short[2048];
        values[7] = new short[22488];
        values[8] = new short[7694];
        values[9] = new short[26090];
        values[10] = new short[10529];
        values[11] = new short[6607];
        values[12] = new short[30707];
        values[13] = new short[18277];
        values[14] = new short[31819];
        values[15] = new short[2510];
        values[16] = new short[28867];
        values[17] = new short[3245];
        values[18] = new short[24757];
        values[19] = new short[8164];
        values[20] = new short[19226];
        values[21] = new short[22705];

        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = Short.MAX_VALUE;
            }
        }

        // Write short `values` as variable byte encoded data to `vbeValues`. Format is: [arrayLen, arrayValue1..arrayValueN]+
        final VariableByteArrayOutput os = new VariableByteArrayOutput();
        for (int i = 0; i < values.length; i++) {
            final short arrayLen = (short) values[i].length;
            os.writeShort(arrayLen);
            for (int j = 0; j < arrayLen; j++) {
                final short arrayValue = values[i][j];
                os.writeShort(arrayValue);
            }
        }
        final byte[] vbeValues = os.toByteArray();

//		System.out.println("data=" + Arrays.toString(vbeValues));    // debug

        // Copy shorts in `vbeValues` via `src` to `dest` into `copiedVbeValues`
        final VariableByteArrayInput src = new VariableByteArrayInput(vbeValues);
        final VariableByteArrayOutput dest = new VariableByteArrayOutput();
        int valuesCopied = 0;
        int tmp = 0;
        int x = 0;
        while (src.available() > 0) {
            final short arrayLen = src.readShort();  // read arrayLen from src

            System.out.println(x + ": arrayLen=" + arrayLen);    // debug

            final boolean skip = tmp % 7 == 0;
            if (skip) {
                // don't copy array to dest
                System.out.println(x + ": skip(" + arrayLen + ")");    // debug
                src.skip(arrayLen);	 // skip over arrayValue(s) in src
            } else {
                // copy to array to dest
                dest.writeShort(arrayLen);	 // write arrayLen to dest
                try {
                    src.copyTo(dest, arrayLen);  // copy arrayValue(s) to dest
                } catch (final ArrayIndexOutOfBoundsException e) {
//					dumpValues(values);
                    System.out.println("tmp=" + tmp);    // debug
                    throw e;
                }
                valuesCopied += arrayLen;
            }
            x++;
            tmp++;
        }
        final byte[] copiedVbeValues = dest.toByteArray();

        // compare number of copied values against values read
        int valuesRead = 0;
        final VariableByteArrayInput is = new VariableByteArrayInput(copiedVbeValues);
        while (is.available() > 0) {
            int count = is.readShort();
            for (int i = 0; i < count; i++) {
                is.readShort();
                valuesRead++;
            }
        }

        return new IntTuple(valuesCopied, valuesRead);
    }

    public static class IntTuple {
        final int expected;
        final int actual;

        public IntTuple(final int expected, final int actual) {
            this.expected = expected;
            this.actual = actual;
        }
    }
}
