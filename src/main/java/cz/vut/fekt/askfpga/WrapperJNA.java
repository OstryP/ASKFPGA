package cz.vut.fekt.askfpga;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface WrapperJNA extends Library {
    WrapperJNA wrapper = Native.load("nfb", WrapperJNA.class);

    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);
}

