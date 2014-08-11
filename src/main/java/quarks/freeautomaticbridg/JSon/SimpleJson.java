/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quarks.freeautomaticbridg.JSon;

import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author eobs
 */
public class SimpleJson implements JSonObject {

    private byte[] jsonObject;
     private static Logger log = Logger.getLogger("JSon");
     private ArrayList<String> values;
     
    public SimpleJson() {
         values=new ArrayList<>();
        
    }
    
    
    public void addValue(String valueName,String value)
    {
        String jsonValue="\""+valueName+"\":"+"\""+value+"\"";
        values.add(jsonValue);
 
    }
    
    @Override
    public int getContentLengt()
    {
        
        return jsonObject.length;
    }
    
    @Override
    public byte[] getContent()
    {
        return jsonObject;
    }

    @Override
    public void build() {
        
        String jsonString="{";
        int fieldCount=0;
        for (String fieald:this.values)
        {
            if (fieldCount==0)
            {
            jsonString=jsonString.concat(fieald);
            }
            else
            {
                jsonString=jsonString.concat(","+fieald);
            }
            
            fieldCount++;
        }
        
        jsonString=jsonString.concat("}");
        
        this.jsonObject=jsonString.getBytes();
    }
    
     
}
