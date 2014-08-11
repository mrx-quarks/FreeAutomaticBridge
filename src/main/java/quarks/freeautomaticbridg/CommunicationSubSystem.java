/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeautomaticbridg;

import java.util.Properties;
import quarks.freeaotomaticbridge.protocoll.WorkingSet;

/**
 *
 * @author eobs
 */
public interface CommunicationSubSystem extends Runnable{
    
    public boolean configSystem(String prefix,Properties properties);
    public String getName(String name);
    public void putWorkingSet(WorkingSet workingSet);
    
}
