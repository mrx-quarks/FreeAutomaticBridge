/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quarks.freeaotomaticbridge.protocoll;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author eobs
 */
public class PackageBuilder {

    private byte packageStructure[] = new byte[14];

    public final static byte CMD_SET = 1;
    public final static byte CMD_GET = 0;
    public final static byte CMD_ACK = 10;
    public final static byte CMD_RES = 20;
    public final static byte CMD_ERR = 20;
    public final static int PACKAGE_SIZE = 14;

    public final static int TYPE_SHORT = 1;
    public final static int TYPE_INT = 2;
    public final static int TYPE_DOUBLE = 3;
    public final static int TYPE_RGB = 4;
    public final static int TYPE_BOOL = 5;
    public final static int TYPE_SW_AND = 6;
    public final static int TYPE_SW_OR = 7;

    public static short uid = 0;

    public PackageBuilder(byte dst_addr, byte src_addr, byte port, byte command, int red,int green,int blue) {
        uid++;
        command=(byte) (command+TYPE_RGB);
        
        
        
        this.packageBuild(dst_addr, src_addr, port, command, uid, (short) 0, (short) 0, (short)0, (short) 0);
        
        // manipulate byte 6 7 8
        packageStructure[6] = (byte) red;
        packageStructure[7] = (byte)  green;
        packageStructure[8] = (byte) blue;
        
    }
    
    public PackageBuilder(byte dst_addr, byte src_addr, byte port, byte command, short value1, short value2, short value3, short value4) {
        uid++;
        this.packageBuild(dst_addr, src_addr, port, command, uid, value1, value2, value3, value4);
    }

    private void packageBuild(byte dst_addr, byte src_addr, byte port, byte command, short uid, short value1, short value2, short value3, short value4) {

        packageStructure[0] = dst_addr;
        packageStructure[1] = src_addr;
        packageStructure[2] = port;
        packageStructure[3] = command;

        // put the short values in the byte array
         //value 1 (4,5)
        packageStructure[5] = (byte) (uid & 0xff);
        packageStructure[4] = (byte) ((uid >> 8) & 0xff);

        //value 1 (4,5)
        packageStructure[7] = (byte) (value1 & 0xff);
        packageStructure[6] = (byte) ((value1 >> 8) & 0xff);

        //value 1 (6,7)
        packageStructure[9] = (byte) (value2 & 0xff);
        packageStructure[8] = (byte) ((value2 >> 8) & 0xff);

         //value 1 (8,9)
        packageStructure[11] = (byte) (value3 & 0xff);
        packageStructure[10] = (byte) ((value3 >> 8) & 0xff);

          //value 1 (10,11)
        packageStructure[13] = (byte) (value4 & 0xff);
        packageStructure[12] = (byte) ((value4 >> 8) & 0xff);

    }

    public PackageBuilder(byte dst_addr, byte src_addr, byte port, byte command, int value1, int value2) {
        // int to short big endian
        uid++;
        this.packageBuild(dst_addr, src_addr, port, command, uid, (short) ((value1 >> 16) & 0xffff), (short) (value1 & 0xffff), (short) ((value2 >> 16) & 0xffff), (short) (value2 & 0xffff));

    }

    public PackageBuilder(byte dst_addr, byte src_addr, byte port, byte command, double value) {
        // extract the post comma values (19 digits)
        uid++;
        ByteBuffer.wrap(packageStructure, 6, 8).putDouble(value);
        packageStructure[0] = dst_addr;
        packageStructure[1] = src_addr;
        packageStructure[2] = port;
        packageStructure[3] = command;

        // put the short values in the byte array
         //value 1 (4,5)
        packageStructure[5] = (byte) (uid & 0xff);
        packageStructure[4] = (byte) ((uid >> 8) & 0xff);

    }

    public PackageBuilder(byte[] packageStructure) {

        System.arraycopy(packageStructure, 0, this.packageStructure, 0, PACKAGE_SIZE);

    }

    public byte[] getPackage() {
        return this.packageStructure;
    }

    public byte getSrcAddress() {

        return packageStructure[1];
    }

    public byte getDestAddress() {
        return packageStructure[0];
    }

    public double getDoubleValue() {

        return ByteBuffer.wrap(packageStructure, 6, 8).getDouble();

    }

    public short getUid() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKAGE_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(packageStructure);

        return buffer.getShort(4);
    }

    public short getShortValue(int pos) {
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(packageStructure);

        return buffer.getShort(6 + (pos * 2));
    }

    public int getIntValue(int pos) {
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(packageStructure);

        return buffer.getInt(6 + (pos * 4));
    }

    public byte getCommand() {
        return packageStructure[3];
    }

    public void uidReset() {
        uid = 0;
    }

}
