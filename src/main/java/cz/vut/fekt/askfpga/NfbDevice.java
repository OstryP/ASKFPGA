package cz.vut.fekt.askfpga;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class NfbDevice extends Structure {
    public NfbDevice(Pointer p) {
        super(p);
        read();
    }
}
