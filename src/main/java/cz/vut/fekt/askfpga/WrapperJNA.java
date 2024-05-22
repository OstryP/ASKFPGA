package cz.vut.fekt.askfpga;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.nio.charset.StandardCharsets;


public interface WrapperJNA extends Library {
    WrapperJNA wrappernfb = Native.load("nfb", WrapperJNA.class);
    WrapperJNA wrapperfdt = Native.load("fdt", WrapperJNA.class);


    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);

    int fdt_path_offset(Pointer fdt, String path);

    Pointer nfb_get_fdt(Pointer dev);

    byte[] fdt_getprop(Pointer fdt, int fdt_offset, String path, IntByReference len);

    public default String getProp(Pointer dev){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        String path = "board-name";
        int fdt_offset = fdt_path_offset(fdt, path);
        int len = 0;
        byte[] vysledek = fdt_getprop(fdt, fdt_offset, path, new IntByReference(len));
        System.out.println(len);
        String s = new String(vysledek, StandardCharsets.UTF_8);
        return s;

    }



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