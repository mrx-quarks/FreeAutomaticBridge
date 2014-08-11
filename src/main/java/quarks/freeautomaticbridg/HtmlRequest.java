/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeautomaticbridg;

import java.io.IOException;
import quarks.freeautomaticbridg.JSon.SimpleJson;
import java.util.HashMap;
import quarks.freeautomaticbridg.JSon.JSonObject;

/**
 *
 * @author eobs
 */
public interface HtmlRequest {
    
    public boolean accept(HashMap<String,String> requestMap,HtmlGetRequestHandler handler) throws IOException;
    
    public void response(HashMap<String,String> requestMap,HtmlGetRequestHandler handler) throws IOException;
    
}
