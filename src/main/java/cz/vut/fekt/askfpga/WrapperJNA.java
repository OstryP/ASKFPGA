package cz.vut.fekt.askfpga;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper který slouží pro komunikaci s knihovnou v C, pomocí JNA
 */
public interface WrapperJNA extends Library {
    WrapperJNA wrappernfb = Native.load("nfb", WrapperJNA.class);
    WrapperJNA wrapperfdt = Native.load("fdt", WrapperJNA.class);
    WrapperJNA wrapperfpga = Native.load("fpga", WrapperJNA.class);


    /**
     * Cesty k jednotlivým komponentům
     */
    public enum Paths{
        BOARD_NAME("/board/", "board-name"),
        SERIAL_NUMBER("/board/", "serial-number"),
        FPGA_UID("/board/", "fpga-uid"),
        CARD_NAME("/firmware/","card-name"),
        PROJECT_NAME("/firmware/","project-name"),
        BUILD_TOOL("/firmware/","build-tool"),
        BUILD_AUTHOR("/firmware/","build-author");

        private final String path;
        private final String label;

        Paths(String path, String label) {
            this.path = path;
            this.label = label;
        }
    }
    int BufferSize = 256;


    //Namapování funkcí z knihovny C
    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);

    int fdt_path_offset(Pointer fdt, String path);

    Pointer nfb_get_fdt(Pointer dev);

    Pointer fdt_getprop(Pointer fdt, int fdt_offset, String path, IntByReference len);


    Pointer nfb_comp_open(Pointer dev, int node);
    void comp_write32(Pointer comp, int offset, int data);
    int comp_read32(Pointer comp, int offset);
    void nfb_comp_close(Pointer comp);

    double get_temperature(Pointer dev);


    boolean fdt_get_path(Pointer fdt, int node_offset, byte[] path, int BUFFER_SIZE);

    int fdt_next_node(Pointer fdt, int offset, IntByReference BUFFER_SIZE);

    Pointer ndp_open_rx_queue(Pointer dev, int num);
    Pointer ndp_open_tx_queue(Pointer dev, int num);

    int ndp_queue_start(Pointer inter);

    int NDP_PACKET_COUNT = 16;

    int ndp_tx_burst_get(Pointer inter, Packet pkts, int NDP_PACKET_COUNT);
    int ndp_rx_burst_get(Pointer inter, Packet pkts, int NDP_PACKET_COUNT);

    void memset(byte[] data, int nula, int dataLength);

    void ndp_tx_burst_flush(Pointer inter);

    void ndp_rx_burst_flush(Pointer inter);

    void ndp_rx_burst_put(Pointer rxq);

    void ndp_tx_burst_put(Pointer tx);

    void ndp_close_tx_queue(Pointer txq);

    void ndp_close_rx_queue(Pointer rxq);


    /**
     * Zrcadlení struktury Packetu vůči knihovně v C
     */
    public class Packet extends Structure {
        public Pointer data;
        public Pointer header;
        public int data_length;
        public int header_length;
        public Packet(Pointer data, Pointer header, int data_length, int header_length) {
            this.data = data;
            this.header = header;
            this.data_length = data_length;
            this.header_length = header_length;
        }

        @Override
        protected List<String> getFieldOrder(){
            return Arrays.asList("data", "header", "data_length", "header_length");
        }

    }

    /**
     * Zrcadlení struktury Node vůči knihovně v C
     */
    public class myNode {
        public String path;
        public int offset;

        public myNode(String path, int offset){
            this.path = path;
            this.offset=offset;
        }
    }

    /**
     * Získání hodnoty specifikované vlastnosti z FPGA karty
     * @param dev Ukazatel na zařízení
     * @param prop Cesta k vlastnosti
     * @return Hodnota vlastnosti
     */
   public default String getProp(Pointer dev, Paths prop){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        int fdt_offset = fdt_path_offset(fdt, prop.path);
        IntByReference len = new IntByReference(0);
        Pointer vysledek = fdt_getprop(fdt, fdt_offset, prop.label, len);
        System.out.println(len.getValue());
        byte[] bytes = vysledek.getByteArray(0, len.getValue());
        System.out.println(bytes[0]);

        String s = new String(bytes, StandardCharsets.UTF_8);
        return s;
    }

    /**
     * Slouží k získání seznamu komponent na FPGA kartě
     * @param dev Ukazatel na zařízení
     * @return Seznam komponent
     */
    public default ArrayList<myNode> print_component_list(Pointer dev){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        int offset = fdt_path_offset(fdt, "/firmware");
        Pointer compatible;
        byte path[] = new byte[256];
        ArrayList<myNode> components = new ArrayList<>();

        while (offset>=0){
            IntByReference len = new IntByReference(0);
            Pointer prop;
            prop = fdt_getprop(fdt, offset, "reg", len);
            if (len.getValue() != -1) {
                fdt_get_path(fdt, offset, path, 256);

                String s = new String(path, StandardCharsets.UTF_8);

                int nullIndex = s.indexOf('\u0000');
                s = s.substring(0, nullIndex);

                //String s = new String(path, StandardCharsets.UTF_8);

                myNode newNode = new myNode(s, offset);

                components.add(newNode);
            }

            offset = fdt_next_node(fdt, offset, null);
        }
        return components;
    }

    /**
     * Slouží k zápisu do specifické kompenenty
     * @param node Kam se zapisuje
     * @param offset Na jaké místo (offset) se zapisuje
     * @param data Co se zapisuje
     */
    public default void nfb_comp_write(int node, int offset, int data){


        Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        if (comp != null && !AppState.getInstance().getOpenedComponents().contains(comp)){
            AppState.getInstance().setOpenedComponents(comp);
        }
        comp_write32(comp, offset, data);
    }

    /**
     * Slouží ke čtení z komponenty
     * @param node Odkud se čte
     * @param offset Z jakého offsetu v rámci komponenty se čte
     * @return Data ze specifikovaného místa
     */
    public default int nfb_comp_read(int node, int offset){
        /*if (!AppState.getInstance().getOpenedComponents().contains(node)) {
            Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        }
        */
        Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        if (comp != null && !AppState.getInstance().getOpenedComponents().contains(comp)){
            AppState.getInstance().setOpenedComponents(comp);
        }
        return comp_read32(comp, offset);
   }

    /**
     * Slouží k zahájení přenosu dat
     * @param fileName Z jakého souboru jsou data čerpána
     * @param num Jakým kanálem jsou data přenášena
     * @throws IOException
     */
    public default void importData(String fileName, int num) throws IOException {
        Pointer txq = ndp_open_tx_queue(AppState.getInstance().getDevPointer(), num);
        if (txq!=null){
            AppState.getInstance().setoTx_que(txq);
        }
        ndp_queue_start(txq);


        String currentDirectory = System.getProperty("user.dir");
        String relativePath = "Data";
        Path directoryPath = java.nio.file.Paths.get(currentDirectory, relativePath).normalize();
        Path filePath = directoryPath.resolve(fileName).normalize();
        byte[] byteFiles = Files.readAllBytes(filePath);

        Packet pkts = new Packet(Pointer.NULL, Pointer.NULL, byteFiles.length, NDP_PACKET_COUNT);

        int ret = ndp_tx_burst_get(txq, pkts, 1);

        for (int i = 0; i<byteFiles.length; i++){
            pkts.data.setByte(i, byteFiles[i]);
        }

        ndp_tx_burst_flush(txq);
    }


    /**
     * Slouží k zobrazaní informací o přenosu dat
     * @return Informace o přenosu
     */
    public default String trafficTX () {

        StringBuilder info = new StringBuilder();

        String selectedItem = "/firmware/mi_bus0/txmac0";

        ArrayList<WrapperJNA.myNode> components = WrapperJNA.wrappernfb.print_component_list(AppState.getInstance().getDevPointer());

        int value;
        int lowPart;
        int highPart;
        long finalValue;

        for(WrapperJNA.myNode component : components){
            if (Objects.equals(component.path, selectedItem)){
                value = 0;
                lowPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                value = 16;
                highPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                finalValue = combineParts(lowPart, highPart);
                info.append("Total Frames Counter: ").append(finalValue).append("\n");

                value = 4;
                lowPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                value = 20;
                highPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                finalValue = combineParts(lowPart, highPart);
                info.append("Sent Octects Counter: ").append(finalValue).append("\n");

                value = 8;
                lowPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                value = 24;
                highPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                finalValue = combineParts(lowPart, highPart);
                info.append("Discarted Frames Counter: ").append(finalValue).append("\n");

                value = 12;
                lowPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                value = 28;
                highPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                finalValue = combineParts(lowPart, highPart);
                info.append("Sent Frames Counter: ").append(finalValue).append("\n");

                return info.toString();
            }
        }
        return null;
    }

    /**
     * Informace o přenosu pro graf
     * @return Kolik bylo přeneseno dat
     */
    public default long trafficSecTX(){
        String selectedItem = "/firmware/mi_bus0/txmac0";

        ArrayList<WrapperJNA.myNode> components = WrapperJNA.wrappernfb.print_component_list(AppState.getInstance().getDevPointer());

        int value;
        int lowPart;
        int highPart;
        long finalValue;

        for(WrapperJNA.myNode component : components){
            if (Objects.equals(component.path, selectedItem)){
                value = 12;
                lowPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                value = 28;
                highPart = WrapperJNA.wrapperfpga.nfb_comp_read(component.offset, value);
                finalValue = combineParts(lowPart, highPart);

                return finalValue;
            }
        }
        return 0;

    }


    /**
     * Slouží ke spojení horní a dolní části čísla, int na long
     * @param lowPart Spodní int
     * @param highPart Horní int
     * @return Spojený long
     */
    public static long combineParts(int lowPart, int highPart) {
        return ((long) highPart << 32) | (lowPart & 0xFFFFFFFFL);
    }



}