package ltd.vastchain.bluetooth.printer;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

/**
 * Created by admin on 2021/12/29.
 */
public class ClsUtils {
    public static BluetoothDevice remoteDevice=null;
    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    @SuppressWarnings("unchecked")
    static public boolean createBond(@SuppressWarnings("rawtypes") Class btClass, BluetoothDevice btDevice)
    {
        try {
            Method createBondMethod = btClass.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            return returnValue.booleanValue();
        } catch (
                Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
