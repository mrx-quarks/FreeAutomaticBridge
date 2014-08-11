/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeaotomaticbridge.protocoll;

 
import quarks.freeautomaticbridg.HtmlGetRequestHandler;

/**
 *
 * @author eobs
 */
public class WorkingSet {

    public WorkingSet(HtmlGetRequestHandler handler,  PackageBuilder packageStructure) {
        this.handler = handler;
        this.uid = packageStructure.getUid();
        this.packageStructure = packageStructure;
    }

    public HtmlGetRequestHandler getHandler() {
        return handler;
    }

    public short getUid() {
        return uid;
    }

    public PackageBuilder getPackageStructure() {
        return packageStructure;
    }
    
    private HtmlGetRequestHandler handler;
    private short uid;
    private PackageBuilder packageStructure;
    
}
