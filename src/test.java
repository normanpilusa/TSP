
import java.util.Random;
import static jdk.nashorn.internal.objects.NativeMath.random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author npilusa
 */
public class test {
    public static void main(String[] args){
        int i = 0;
        Random generator = new Random();
        while(i < 500){
            System.out.println(generator.nextInt(50));
            i++;
    }
    }
}
