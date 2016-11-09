/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regular_class;

/**
 *
 * @author wl2-lam
 */
public class Creditcheck {
    private final int[] d = new int[16];
    
    public boolean credircheck(String s){
          try {

            
            for (int i = 0; i < s.length(); i++) {
                d[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
            }
            for (int x = 0; x < d.length; x += 2) {
                int check = d[x] + d[x];

                if (check >= 10) {
                    check = check - 9;
                }

                d[x] = check;

            }

            int cal = (d[0] + d[1] + d[2] + d[3] + d[4] + d[5] + d[6] + d[7] + d[8] + d[9] + d[10] + d[11] + d[12] + d[13] + d[14] + d[15]);
            cal = cal % 10;

            if (cal == 0) {
               
                return true;

            } else {
              return false;
            }
            
        } catch (NumberFormatException e) {
            return false;
        }
         
    }
    
}
