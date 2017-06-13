
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
public class MethodTests {

    private static City[] cities;
    private static int cityCount;
    private static Random generator = new Random();
    
    
    /* Testing for crossover method
     * Crossing over parents using PMX
     */
    public static Chromosome crossover(Chromosome parentA, Chromosome parentB){
        Chromosome child = new Chromosome(cities); //to avoid null deferencing, this gets changed
        int[] cts = new int[cityCount];
        String pA = "", pB="",c="";
        
        //====================================
        for(int i=0;i<cityCount; i++){
            pA = pA+parentA.getCity(i)+"| ";
        }
        
        for(int i=0;i<cityCount; i++){
            pB = pB+parentB.getCity(i)+"| ";
        }
        //====================================
        
        //Pick random point to crossover
        int point = (int)(random(cityCount)*50);
        
        for(int i = 0; i < cityCount; i++){
            if(i < point){
                //Get first portion from parentA
                cts[i] = parentA.getCity(i);
            }
            else{
                //Fill child with parentB cities
                for(int city = 0; city < cityCount; city++){ //This is n2 complexity
                    int fromB = parentB.getCity(city);
                    
                    //Iterate through child content 
                    for(int j = 0; j < cityCount; j++){
                        if(cts[j] == fromB){
                            break;//Pick next city from parentB
                        }
                        else{
                            if(j == i){//insert from point
                                cts[i] = fromB;
                                i++;//next slot in child
                                break;//Pick next city from parentB
                            }
                        }
                    }
                }
            }
        }
        child.setCities(cts);
        child.calculateCost(cities);
        
        
        for(int i=0;i<cityCount; i++){
            c = c+child.getCity(i)+"| ";
        }
        System.out.println("ParentA: "+pA+"\nParentB: "+pB+"\nChild  : "+c+"\n");
        return child;
    }
    
    
    
    
    /*
     * Mutates a chromosome
     */
    public static Chromosome mutate(Chromosome chrom){
        int newCity = generator.nextInt(50);
        int cityChanged;
        
        //====================================
        String c = "", c2="",vc="", nc="";
        
        for(int i=0; i<cityCount;i++){
            c = c+chrom.getCity(i)+"| ";
        }
        //====================================
        
        
        for(int position = 0; position < cityCount; position++){
            
            //Use mutation rate of 15%
            if(random(1) < 0.015){
                cityChanged = chrom.getCity(position);
                vc = vc+position+"| ";//for testing purposes
                nc = nc+newCity+"| ";
                
                for(int swap=0; swap< cityCount; swap++){
                    if(chrom.getCity(swap)== newCity){
                        chrom.setCity(swap,cityChanged);
                        //cts[swap] = cityChanged;
                        //cts[position] = newCity;
                        chrom.setCity(position, newCity); //use swap mutation
                        break;
                    }
                }
                newCity = generator.nextInt(50);//Generate a new random city
            }
        }
        
        chrom.calculateCost(cities);
        //================================
        for(int i=0; i<cityCount;i++){
            c2 = c2+chrom.getCity(i)+"| ";
        }
        System.out.println("City swaps: "+vc+"\nNew Cities: "+nc+"\nThe input : "+c+"\nThe output: "+c2+"\n");
        //================================
        
        return chrom;
    }
}
