
import java.util.ArrayList;
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

    public static void main(String[] args) {
       int[] arr = new int[10];
       ArrayList<Integer> population = new ArrayList<>();
       
       Random generator = new Random();
       //Create a population
       for(int i = 0; i<10; i++){
           arr[i] = i;
       }
       
       //Sort
       population.add(arr[0]);
       
       for(int i = 0; i < 10; i++){
           int current = arr[i];
           
           //Create new population
           for(int j = i+1; j < 10; j++){
               if(!population.contains(arr[i]) && current<1){
                   
               }
           }
       }
       
       
    }
}
