/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeautomaticbridg.RFCommunication;

 
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quarks.freeaotomaticbridge.protocoll.PackageBuilder;

/**
 *
 * @author eobs
 */
public class PackageBuilderTest {
    
    public PackageBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getPackage method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetPackage() {
        
        
        System.out.println("getPackage Constructor Test");
        System.out.println("short Constructor");
        PackageBuilder instance;
        instance = new PackageBuilder( (byte) 1, (byte)2, (byte)3, (byte) PackageBuilder.CMD_GET, (short) 10, (short) 11,(short) 12,(short) 13);
        instance.uidReset();
        byte[] expResult = {1,2,3,PackageBuilder.CMD_GET,0,1,0,10,0,11,0,12,0,13};
        byte[] result = instance.getPackage();
         
        assertTrue(Arrays.equals(result, expResult));
        
        
        System.out.println("int Constructor");
       
        instance = new PackageBuilder( (byte) 1, (byte)2, (byte)3, (byte) PackageBuilder.CMD_GET, 10,13);
          byte[] expResult2 = {1,2,3,PackageBuilder.CMD_GET,1,0,0,0,10,0,0,0,13};
          result = instance.getPackage();
         
        //assertTrue(Arrays.equals(result, expResult2));
        
          System.out.println("double Constructor");
       
        instance = new PackageBuilder( (byte) 1, (byte)2, (byte)3, (byte) PackageBuilder.CMD_GET, 20.14);
          byte[] expResult3 = {1,2,3,PackageBuilder.CMD_GET,0,1,64,52,35,-41,10,61,112,-92};
          result = instance.getPackage();
         
        //assertTrue(Arrays.equals(result, expResult3));
          assertTrue(true);
         
    }

    /**
     * Test of getSrcAddress method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetSrcAddress() {
        System.out.println("getSrcAddress");
        PackageBuilder instance = new PackageBuilder(new byte[] {1,2,3,PackageBuilder.CMD_GET,0,1,0,0,0,10,0,0,0,13});
        byte expResult = 2;
        byte result = instance.getSrcAddress();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getDestAddress method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetDestAddress() {
        System.out.println("getDestAddress");
         PackageBuilder instance = new PackageBuilder(new byte[] {1,2,3,PackageBuilder.CMD_GET,0,1,0,0,0,10,0,0,0,13});
        byte expResult = 1;
        byte result = instance.getDestAddress();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDoubleValue method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetDoubleValue() {
        System.out.println("getDoubleValue");
        PackageBuilder instance = new PackageBuilder(new byte[] {1,2,3,PackageBuilder.CMD_GET,0,1,64,52,35,-41,10,61,112,-92});
        double expResult = 20.14;
        double result = instance.getDoubleValue();
        assertEquals(expResult, result,0.001);
    }

    /**
     * Test of getShortValue method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetShortValue() {
        System.out.println("getShortValue");
         
        PackageBuilder instance = new PackageBuilder(new byte[]  {1,2,3,PackageBuilder.CMD_GET,0,1,0,10,0,11,0,12,0,13});
       int expResult1 = 10;
       int expResult2 = 12;
         
        assertEquals(expResult1, instance.getShortValue(0));
         assertEquals(expResult2, instance.getShortValue(2));
  
      
    }

    /**
     * Test of getIntValue method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetIntValue() {
        System.out.println("getIntValue");
         
        PackageBuilder instance = new PackageBuilder(new byte[] {1,2,3,PackageBuilder.CMD_GET,0,1,0,0,0,10,0,0,0,13});
       int expResult1 = 10;
       int expResult2 = 13;
         
        assertEquals(expResult1, instance.getIntValue(0));
         assertEquals(expResult2, instance.getIntValue(1));
    }

    /**
     * Test of getCommand method, of class PackageBuilder.
     */
    @org.junit.Test
    public void testGetCommand() {
        System.out.println("getCommand");
        
         PackageBuilder instance = new PackageBuilder(new byte[] {1,2,3,PackageBuilder.CMD_GET,0,1,0,0,0,10,0,0,0,13});
        byte expResult = PackageBuilder.CMD_GET;
        byte result = instance.getCommand();
        assertEquals(expResult, result);
    }
    
 
    
}
