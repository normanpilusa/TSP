
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Time;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.*;

import javax.swing.*;
import static jdk.nashorn.internal.objects.NativeMath.random;

public class TSP {

    private static final int cityShiftAmount = 60; //DO NOT CHANGE THIS.

    /**
     * How many cities to use.
     */
    protected static int cityCount;

    /**
     * How many chromosomes to use.
     */
    protected static int populationSize = 100; //DO NOT CHANGE THIS.

    /**
     * The part of the population eligable for mating.
     */
    protected static int matingPopulationSize;

    /**
     * The part of the population selected for mating.
     */
    protected static int selectedParents;

    /**
     * The current generation
     */
    protected static int generation;

    /**
     * The list of cities (with current movement applied).
     */
    protected static City[] cities;

    /**
     * The list of cities that will be used to determine movement.
     */
    private static City[] originalCities;

    /**
     * The list of chromosomes.
     */
    protected static Chromosome[] chromosomes;

    /**
     * Frame to display cities and paths
     */
    private static JFrame frame;

    /**
     * Integers used for statistical data
     */
    private static double min;
    private static double avg;
    private static double max;
    private static double sum;
    private static double genMin;

    /**
     * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
     */
    private static int width = 600;
    private static int height = 600;

    private static Panel statsArea;
    private static TextArea statsText;
    private static Random generator = new Random();


    /*
     * Writing to an output file with the costs.
     */
    private static void writeLog(String content) {
        String filename = "results.out";
        FileWriter out;

        try {
            out = new FileWriter(filename, true);
            out.write(content + "\n");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     *  Deals with printing same content to System.out and GUI
     */
    private static void print(boolean guiEnabled, String content) {
        if (guiEnabled) {
            statsText.append(content + "\n");
        }

        System.out.println(content);
    }

    public static void evolve() {
        /*Create a mating pool*/
        Chromosome[] matingPool = new Chromosome[100];
        Chromosome.sortChromosomes(chromosomes, populationSize);//sort all chromosomes

        int randomChrom;
        matingPopulationSize = 100;

        for (int i = 0; i < 50; i++) {
            matingPool[i] = chromosomes[i]; //Take top 50

            //Add a random chromosome from the rest of the population
            randomChrom = (int) (random(100) * 50);//Pick from 26 to 100th
            matingPool[99 - i] = chromosomes[randomChrom];
        }

        /*Select two parents*/
        int a, b;
        Chromosome parentA, parentB;
        Chromosome childA, childB;

        int oPar1 = 49, oPar2 = 50;//Start of parents to be replaced

        for (int i = 0; i < matingPopulationSize; i++) {

            a = (int) (random(50) * 100);//between 0
            b = (int) (random(50) * 100);//and 50

            parentA = matingPool[a];
            parentB = matingPool[b];

            /*Crossover parents using pmx*/
            childA = crossover(parentA, parentB);
            childB = crossover(parentB, parentA);//switch parents around

            /*Mutate a random child using nearest neighbor*/
            if (generator.nextFloat() < 0.15) {
                childA = nearestNeighbor(childA);
            }

            /*Replace weak parents in old generation*/
            oPar1 = generator.nextInt(50) + 49;
            oPar2 = generator.nextInt(50) + 50;
            if (childA.getCost() < chromosomes[oPar1].getCost()) {
                chromosomes[oPar1] = childA;
            }

            if (childB.getCost() < chromosomes[oPar2].getCost()) {
                chromosomes[oPar2] = childB;
            }

        }

    }

    /**
     * Crossing over parents using Partial Mutation Crossover
     */
    public static Chromosome crossover(Chromosome parentA, Chromosome parentB) {
        Chromosome child = new Chromosome(cities); //to avoid null deferencing, this gets changed
        int[] cts = new int[cityCount];

        //Pick random point to crossover
        int point = (int) (random(cityCount) * 50);

        for (int i = 0; i < cityCount; i++) {
            if (i < point) {
                //Get first portion from parentA
                cts[i] = parentA.getCity(i);
            } else {
                //Fill child with parentB cities
                for (int city = 0; city < cityCount; city++) { //This is n2 complexity
                    int fromB = parentB.getCity(city);

                    //Iterate through child content 
                    for (int j = 0; j < cityCount; j++) {
                        if (cts[j] == fromB) {
                            break;//Pick next city from parentB
                        } else if (j == i) {//insert from point
                            cts[i] = fromB;
                            i++;//next slot in child
                            break;//Pick next city from parentB
                        }
                    }
                }
            }
        }
        child.setCities(cts);
        child.calculateCost(cities);

        return child;
    }

    /**
     * Nearest neighbor for initial population
     */
    public static Chromosome nearestNeighbor(Chromosome chrom) {
        int valueCurrentCity;
        int valueNearest;
        int nearestDist;
        int indexNearest;
        int temp;
        int dist;

        for (int currentCity = 0; currentCity < (cityCount - 1); currentCity++) {
            valueCurrentCity = chrom.getCity(currentCity);//Get the current city
            //Assume next one is the nearest
            valueNearest = chrom.getCity(currentCity + 1);
            nearestDist = cities[valueCurrentCity].proximity(cities[valueNearest]);
            indexNearest = currentCity + 1;

            for (int nextCity = currentCity + 2; nextCity < cityCount; nextCity++) {
                //look for nearest
                dist = cities[valueCurrentCity].proximity(cities[chrom.getCity(nextCity)]);// pick next city

                if (dist < nearestDist) {
                    nearestDist = dist;
                    valueNearest = chrom.getCity(nextCity);
                    indexNearest = nextCity;
                }
            }

            //Swap cities
            temp = chrom.getCity(currentCity + 1);
            chrom.setCity(currentCity + 1, valueNearest);
            chrom.setCity(indexNearest, temp);
        }
        return chrom;
    }

    /**
     * Sort population using nearest neighbour
     *
     * @param chrom
     * @return
     */
    public static void nnSort(Chromosome[] chromos) {

        for (int i = 0; i < chromos.length; i++) {
            chromos[i] = nearestNeighbor(chromos[i]);
        }
    }

    /**
     *
     * @param chrom
     * @return
     */
    public static void nearest(Chromosome[] chromos) {

    }

    /*
     * Mutates a chromosome. Swap mutation
     */
    public static Chromosome mutate(Chromosome chrom) {
        int newCity = generator.nextInt(50);
        int cityChanged;
        int[] cts = new int[cityCount];

        /* Make a copy of chrom*/
        for (int i = 0; i < cityCount; i++) {
            cts[i] = chrom.getCity(i);
        }

        Chromosome mutated = new Chromosome(cities);
        mutated.setCities(cts);
        int changePosition = generator.nextInt(50);

        for (int position = 0; position < cityCount; position++) {

            //Use mutation rate of 15%
            if (generator.nextFloat() < 0.10) {
                cityChanged = mutated.getCity(position);

                for (int swap = 0; swap < cityCount; swap++) {
                    if (mutated.getCity(swap) == newCity) {
                        mutated.setCity(swap, cityChanged);
                        mutated.setCity(position, newCity); //use swap mutation
                        break;
                    }
                }
                newCity = generator.nextInt(50);//Generate a new random city
            }
        }
        mutated.calculateCost(cities);

        //Return better solution crossover only or mutation+crossover
        if (mutated.getCost() > chrom.getCost()) {
            return chrom; //Do not mutate
        } else {
            return mutated;//Mutate
        }
    }

    /**
     * Update the display
     */
    public static void updateGUI() {
        Image img = frame.createImage(width, height);
        Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (true && (cities != null)) {
            for (int i = 0; i < cityCount; i++) {
                int xpos = cities[i].getx();
                int ypos = cities[i].gety();
                g.setColor(Color.green);
                g.fillOval(xpos - 5, ypos - 5, 10, 10);
            }

            g.setColor(Color.gray);
            for (int i = 0; i < cityCount; i++) {
                int icity = chromosomes[0].getCity(i);
                if (i != 0) {
                    int last = chromosomes[0].getCity(i - 1);
                    g.drawLine(
                            cities[icity].getx(),
                            cities[icity].gety(),
                            cities[last].getx(),
                            cities[last].gety());
                }
            }

            int homeCity = chromosomes[0].getCity(0);
            int lastCity = chromosomes[0].getCity(cityCount - 1);

            //Drawing line returning home
            g.drawLine(
                    cities[homeCity].getx(),
                    cities[homeCity].gety(),
                    cities[lastCity].getx(),
                    cities[lastCity].gety());
        }
        frame.getGraphics().drawImage(img, 0, 0, frame);
    }

    private static City[] LoadCitiesFromFile(String filename, City[] citiesArray) {
        ArrayList<City> cities = new ArrayList<City>();
        try {
            FileReader inputFile = new FileReader(filename);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                String[] coordinates = line.split(", ");
                cities.add(new City(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
            }

            bufferReader.close();

        } catch (Exception e) {
            System.out.println("Error while reading file line by line:" + e.getMessage());
        }

        citiesArray = new City[cities.size()];
        return cities.toArray(citiesArray);
    }

    private static City[] MoveCities(City[] cities) {
        City[] newPositions = new City[cities.length];
        Random randomGenerator = new Random();

        for (int i = 0; i < cities.length; i++) {
            int x = cities[i].getx();
            int y = cities[i].gety();

            int position = randomGenerator.nextInt(5);

            if (position == 1) {
                y += cityShiftAmount;
            } else if (position == 2) {
                x += cityShiftAmount;
            } else if (position == 3) {
                y -= cityShiftAmount;
            } else if (position == 4) {
                x -= cityShiftAmount;
            }

            newPositions[i] = new City(x, y);
        }

        for (int i = 0; i < populationSize; i++) {
            chromosomes[i].calculateCost(newPositions);
        }

        return newPositions;
    }

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String currentTime = df.format(today);

        int runs;
        boolean display = false;
        String formatMessage = "Usage: java TSP 1 [gui] \n java TSP [Runs] [gui]";

        if (args.length < 1) {
            System.out.println("Please enter the arguments");
            System.out.println(formatMessage);
            display = false;
        } else {

            if (args.length > 1) {
                display = true;
            }

            try {
                cityCount = 50;
                populationSize = 100;
                runs = Integer.parseInt(args[0]);

                if (display) {
                    frame = new JFrame("Traveling Salesman");
                    statsArea = new Panel();

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setSize(width + 300, height);
                    frame.setResizable(false);
                    frame.setLayout(new BorderLayout());

                    statsText = new TextArea(35, 35);
                    statsText.setEditable(false);

                    statsArea.add(statsText);
                    frame.add(statsArea, BorderLayout.EAST);

                    frame.setVisible(true);
                }

                min = 0;
                avg = 0;
                max = 0;
                sum = 0;

                originalCities = cities = LoadCitiesFromFile("/home/npilusa/NetBeansProjects/TSP/src/CityList.txt", cities);

                writeLog("Run Stats for experiment at: " + currentTime);
                for (int y = 1; y <= runs; y++) {
                    genMin = 0;
                    print(display, "Run " + y + "\n");

                    // create the initial population of chromosomes
                    chromosomes = new Chromosome[populationSize];
                    for (int x = 0; x < populationSize; x++) {
                        chromosomes[x] = new Chromosome(cities);
                    }

                    generation = 0;
                    double thisCost = 0.0;
                    //init with a heuristic
                    nnSort(chromosomes);//sort by nearest neighbor

                    while (generation < 100) {
                        evolve();
                        if (generation % 5 == 0) {
                            cities = MoveCities(originalCities); //Move from original cities, so they only move by a maximum of one unit.
                        }
                        generation++;

                        Chromosome.sortChromosomes(chromosomes, populationSize);
                        double cost = chromosomes[0].getCost();
                        thisCost = cost;

                        if (thisCost < genMin || genMin == 0) {
                            genMin = thisCost;
                        }

                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);

                        print(display, "Gen: " + generation + " Cost: " + (int) thisCost);

                        if (display) {
                            updateGUI();
                        }
                    }

                    writeLog(genMin + "");

                    if (genMin > max) {
                        max = genMin;
                    }

                    if (genMin < min || min == 0) {
                        min = genMin;
                    }

                    sum += genMin;

                    print(display, "");
                }

                avg = sum / runs;
                print(display, "Statistics after " + runs + " runs");
                print(display, "Solution found after " + generation + " generations." + "\n");
                print(display, "Statistics of minimum cost from each run \n");
                print(display, "Lowest: " + min + "\nAverage: " + avg + "\nHighest: " + max + "\n");

            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }
}
