package cz.vut.fekt.askfpga;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.nio.charset.StandardCharsets;


public interface WrapperJNA extends Library {
    WrapperJNA wrappernfb = Native.load("nfb", WrapperJNA.class);
    WrapperJNA wrapperfdt = Native.load("fdt", WrapperJNA.class);

    /*enum Paths{

    };
*/
    int BufferSize = 256;

    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);

    int fdt_path_offset(Pointer fdt, String path);

    Pointer nfb_get_fdt(Pointer dev);

    Pointer fdt_getprop(Pointer fdt, int fdt_offset, String path, IntByReference len);


    int nfb_comp_find(Pointer dev, Pointer compatible, int index);

    nfb_comp_open(Pointer dev, int node);
    void nfb_comp_write32(Pointer comp, int offset, byte data);
    int nfb_comp_read32(Pointer comp, int offset);

    boolean fdt_get_path(Pointer fdt, int node_offset, String path, int BUFFER_SIZE);

    int fdt_next_node(Pointer fdt, int offset, IntByReference BUFFER_SIZE);





   /* public default String getProp(Pointer dev){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        int fdt_offset = fdt_path_offset(fdt, paths.path);
        IntByReference len = new IntByReference(0);
        Pointer vysledek = fdt_getprop(fdt, fdt_offset, paths.label, len);
        System.out.println(len.getValue());
        byte[] bytes = vysledek.getByteArray(0, len.getValue());
        System.out.println(bytes[0]);

        String s = new String(bytes, StandardCharsets.UTF_8);
        return s;

        //string do utf8, int ze 4 bytu

    }*/




    public default void print_component_list(Pointer dev){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        int offset = fdt_path_offset(fdt, "/firmware");
        Pointer compatible;
        String path = new String(new byte[256]);

        while (offset>=0){
            IntByReference len = new IntByReference(0);
            Pointer prop;
            prop = fdt_getprop(fdt, offset, "reg", len);
            if (len.getValue() != -1) {
                fdt_get_path(fdt, offset, path, 256);
                compatible = fdt_getprop(fdt, offset, "compatible", null);

                System.out.println(compatible + path + (prop.getByte(0) << 24) + (prop.getByte(1) << 16) + (prop.getByte(2) << 8) + (prop.getByte(3)));

                System.out.println(nfb_comp_find(dev, compatible, 0) + offset);

            }

            offset = fdt_next_node(fdt, offset, null);




        }

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