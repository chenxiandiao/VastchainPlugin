//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package zpCPCLSDK.zpCPCLSDK;

import android.graphics.Bitmap;

public interface PrinterInterface {
    int MSG_CONNECT_START = 1;
    int MSG_CONNECT_ING = 2;
    int MSG_CONNECT_FINISH = 3;
    int MSG_DISCONNECTED = 4;

    boolean connect(String var1);

    void disconnect();

    boolean print(int var1, int var2);

    void pageSetup(int var1, int var2);

    void drawBox(int var1, int var2, int var3, int var4, int var5);

    void drawLine(int var1, int var2, int var3, int var4, int var5, boolean var6);

    void drawText(int var1, int var2, String var3, int var4, int var5, int var6, boolean var7, boolean var8);

    void drawText(int var1, int var2, int var3, int var4, String var5, int var6, int var7, int var8, boolean var9, boolean var10);

    void drawBarCode(int var1, int var2, String var3, int var4, boolean var5, int var6, int var7);

    void drawQrCode(int var1, int var2, String var3, int var4, int var5, int var6);

    void drawGraphic(int var1, int var2, int var3, int var4, Bitmap var5);

    String printerStatus();

    void drawINVERSE(int var1, int var2, int var3, int var4, int var5);

    int GetStatus();

    void Write(byte[] var1);

    void Read(byte[] var1, int var2, int var3);

    void feed();

    String r_data();

    public static enum BarcodeType {
        JAN3_EAN13,
        JAN8_EAN8,
        CODE39,
        CODE93,
        CODE128,
        CODABAR,
        ITF,
        UPC_A,
        UPC_E,
        EAN13Plus2,
        EAN13Plus5,
        EAN8Plus2,
        EAN8Plus5,
        UPCAPlus2,
        UPCAPlus5,
        UPCEPlus2,
        UPCEPlus5,
        Postnet,
        MSI,
        QR;

        private BarcodeType() {
        }
    }

//    public static enum PrintManufacturer {
//        private PrintManufacturer() {
//        }
//    }
}
