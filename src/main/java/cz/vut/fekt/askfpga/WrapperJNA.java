package cz.vut.fekt.askfpga;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


public interface WrapperJNA extends Library {
    WrapperJNA wrappernfb = Native.load("nfb", WrapperJNA.class);
    WrapperJNA wrapperfdt = Native.load("fdt", WrapperJNA.class);


    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);

    /*fdt = nfb_get_fdt(dev);
    fdt_offset = fdt_path_offset(fdt, "/firmware/");

    fdt_getprop(fdt, fdt_offset, "project-name", &len);

    Pointer fdt_getprop()
    */


    /*
    project: Project name
    prop = fdt_getprop(fdt, fdt_offset, "project-name", &len);

    project-version: Project version
    prop = fdt_getprop(fdt, fdt_offset, "project-version", &len);

    card: Card name
    prop = fdt_getprop(fdt, fdt_offset, "card-name", &len);
    */


}