package cz.vut.fekt.askfpga;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public interface WrapperJNA extends Library {
    WrapperJNA wrappernfb = Native.load("nfb", WrapperJNA.class);
    WrapperJNA wrapperfdt = Native.load("fdt", WrapperJNA.class);

    WrapperJNA wrapperfpga = Native.load("fpga", WrapperJNA.class);


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

    Pointer nfb_open(String name);

    void nfb_close(Pointer dev);

    int fdt_path_offset(Pointer fdt, String path);

    Pointer nfb_get_fdt(Pointer dev);

    Pointer fdt_getprop(Pointer fdt, int fdt_offset, String path, IntByReference len);


    Pointer nfb_comp_open(Pointer dev, int node);
    void nfb_comp_write32(Pointer comp, int offset, int data);
    byte nfb_comp_read32(Pointer comp, int offset);
    void nfb_comp_close(Pointer comp);

    double nc_adc_sensors_get_temp(Pointer dev);


    boolean fdt_get_path(Pointer fdt, int node_offset, byte[] path, int BUFFER_SIZE);

    int fdt_next_node(Pointer fdt, int offset, IntByReference BUFFER_SIZE);

    Pointer ndp_open_rx_queue(Pointer dev, int num);
    Pointer ndp_open_tx_queue(Pointer dev, int num);

    int ndp_queue_start(Pointer inter);

    int NDP_PACKET_COUNT = 16;

    int ndp_tx_burst_get(Pointer inter, Packet[] pkts, int NDP_PACKET_COUNT);
    int ndp_rx_burst_get(Pointer inter, Packet[] pkts, int NDP_PACKET_COUNT);

    void memset(byte[] data, int nula, int dataLength);

    void ndp_tx_burst_flush(Pointer inter);

    void ndp_rx_burst_flush(Pointer inter);

    void ndp_rx_burst_put(Pointer rxq);

    void ndp_tx_burst_put(Pointer tx);

    void ndp_close_tx_queue(Pointer txq);

    void ndp_close_rx_queue(Pointer rxq);


    public class Packet {
        public byte[] data;
        public int header;
        public int data_length;
        public int header_length;

        /*public Packet(int data, int header, int data_length, int header_length) {
            this.data = data;
            this.header = header;
            this.data_length = data_length;
            this.header_length = header_length;
        }*/

        public Packet() {

        }
    }

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

    public default ArrayList<String> print_component_list(Pointer dev){
        Pointer fdt;
        fdt = nfb_get_fdt(dev);
        int offset = fdt_path_offset(fdt, "/firmware");
        Pointer compatible;
        byte path[] = new byte[256];
        ArrayList<String> components = new ArrayList<>();

        while (offset>=0){
            IntByReference len = new IntByReference(0);
            Pointer prop;
            prop = fdt_getprop(fdt, offset, "reg", len);
            if (len.getValue() != -1) {
                fdt_get_path(fdt, offset, path, 256);
                //uložit offset, ten je důležitý
                //v dropdown menu zobrazit třeba path
                //compatible = fdt_getprop(fdt, offset, "compatible", null);

                //String s = new String(path, StandardCharsets.UTF_8);
                String s = Arrays.toString(path);

                /*System.out.println(compatible.getString(0) + s + (prop.getByte(0) << 24) + ((prop.getByte(1) & 0xff) << 16) + ((prop.getByte(2) & 0xff) << 8) + ((prop.getByte(3) & 0xff)));
                components.add(compatible.getString(0) + s + (prop.getByte(0) << 24) + ((prop.getByte(1) & 0xff) << 16) + ((prop.getByte(2) & 0xff) << 8) + ((prop.getByte(3) & 0xff)));*/

                components.add(s);
            }

            offset = fdt_next_node(fdt, offset, null);
        }
        return components;
    }
    public default void nfb_comp_write(int node, int offset, int data){


        Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        if (comp != null && !AppState.getInstance().getOpenedComponents().contains(comp)){
            AppState.getInstance().setOpenedComponents(comp);
        }
        nfb_comp_write32(comp, offset, data);
    }

    public default int nfb_comp_read(int node, int offset){
        /*if (!AppState.getInstance().getOpenedComponents().contains(node)) {
            Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        }
        */
        Pointer comp = nfb_comp_open(AppState.getInstance().getDevPointer(), node);
        if (comp != null && !AppState.getInstance().getOpenedComponents().contains(comp)){
            AppState.getInstance().setOpenedComponents(comp);
        }
        return nfb_comp_read32(comp, offset);
   }

   public default void sendData(String queType, int num) throws InterruptedException {
        if(Objects.equals(queType, "rxq")){
            Pointer rxq = ndp_open_rx_queue(AppState.getInstance().getDevPointer(), num);
            if (rxq!=null){
                AppState.getInstance().setoRx_que(rxq);
            }
            ndp_queue_start(rxq);
            Packet[] pkts = new Packet[NDP_PACKET_COUNT];
            for (int i = 0; i < NDP_PACKET_COUNT; i++) {
                pkts[i] = new Packet();
                pkts[i].data_length = 64 + i;
                pkts[i].data = new byte[64 + i];
                pkts[i].header_length = 0;
                pkts[i].data[5] = 1;
            }

            int ret = ndp_rx_burst_get(rxq, pkts, NDP_PACKET_COUNT);

            for (int i = 0; i < ret; i++) {
                memset(pkts[i].data, 0, pkts[i].data_length);
                /* Pretend IPv4 */
                pkts[i].data[13] = 0x08;
            }

            ndp_rx_burst_flush(rxq);



            for (int bursts = 0; bursts < 32; bursts++) {
                /* Let the library fill at most NDP_PACKET_COUNT, but it may be less */
                ret = ndp_rx_burst_get(rxq, pkts, NDP_PACKET_COUNT);
                if (ret == 0) {
                    Thread.sleep(10);
                    continue;
                }

                for (int i = 0; i < ret; i++) {
                    /* If the metadata is present, it typically holds packet timestamp at offset 0 */
                    if (pkts[i].header_length >= 8){
                        //printf("Timestamp: %lld\n", *((uint64_t*) (pkts[i].header + 0)));
                    }

                    if ((bursts % 5) == 4){
                        ndp_rx_burst_put(rxq);
                    }
                }
            }
            ndp_rx_burst_put(rxq);
        }

       if(Objects.equals(queType, "txq")){
           Pointer txq = ndp_open_tx_queue(AppState.getInstance().getDevPointer(), num);
           if (txq!=null){
               AppState.getInstance().setoTx_que(txq);
           }
           ndp_queue_start(txq);
           Packet[] pkts = new Packet[NDP_PACKET_COUNT];
           for (int i = 0; i < NDP_PACKET_COUNT; i++) {
               pkts[i] = new Packet();
               pkts[i].data_length = 64 + i;
               pkts[i].data = new byte[64 + i];
               pkts[i].header_length = 0;
               pkts[i].data[5] = 1;
           }

           int ret = ndp_tx_burst_get(txq, pkts, NDP_PACKET_COUNT);

           for (int i = 0; i < ret; i++) {
               memset(pkts[i].data, 0, pkts[i].data_length);
               /* Pretend IPv4 */
               pkts[i].data[13] = 0x08;
           }

           ndp_tx_burst_flush(txq);



           for (int bursts = 0; bursts < 32; bursts++) {
               /* Let the library fill at most NDP_PACKET_COUNT, but it may be less */
               ret = ndp_tx_burst_get(txq, pkts, NDP_PACKET_COUNT);
               if (ret == 0) {
                   Thread.sleep(10);
                   continue;
               }

               for (int i = 0; i < ret; i++) {
                   /* If the metadata is present, it typically holds packet timestamp at offset 0 */
                   if (pkts[i].header_length >= 8){
                       //printf("Timestamp: %lld\n", *((uint64_t*) (pkts[i].header + 0)));
                   }

                   if ((bursts % 5) == 4){
                       ndp_tx_burst_put(txq);
                   }
               }
           }
           ndp_tx_burst_put(txq);
       }
   }




}