import java.io.*;
import java.util.*;

public class PairMatrix {
    private static String domainChars="abcdefghijklmnopqrstuvwxyz0123456789-_";
    public static Map<Character,Integer> charIndex=null;

    static{
        charIndex=new HashMap<Character, Integer>();
        for(int i=0;i<domainChars.length();i++)
            charIndex.put(domainChars.charAt(i),i);
    }

    public static void saveMatrix(String textFile,String matrixPath) {
        double[][] matrix=new double[domainChars.length()][domainChars.length()];
        //init matrix
        for(int i=0;i<domainChars.length();i++)
            Arrays.fill(matrix[i],10.0);

        // read cropos and count  pair
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(PairMatrix.class.getResourceAsStream(textFile)));
            String line = null;
            while ((line = br.readLine()) != null) {
                line=line.toLowerCase();
                for (int i = 0; i < line.length() - 1; i++) {
                    Integer a = charIndex.get(line.charAt(i));
                    Integer b = charIndex.get(line.charAt(i + 1));
                    if (a != null && b != null) {
                        matrix[a][b] += 1.0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        double[] rowTotal= new double[domainChars.length()];
        for(int i=0;i<domainChars.length();i++){
            double s=0;
            for(int j=0;j<domainChars.length();j++){
                s+=matrix[i][j];
            }
            rowTotal[i]=s;
        }

        for(int i=0;i<domainChars.length();i++){
            for(int j=0;j<domainChars.length();j++){
                matrix[i][j]=Math.log(matrix[i][j]/rowTotal[i]);
            }
        }

        BufferedWriter bw=null;
        try {
            bw=new BufferedWriter(new FileWriter(new File(matrixPath)));
            for(int i=0;i<domainChars.length();i++){
                for(int j=0;j<domainChars.length();j++){
                    if(j!=domainChars.length()-1){
                        bw.write(matrix[i][j]+"\t");
                    }else{
                        bw.write(matrix[i][j]+"\r\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static double[][] getMatrix(String matPath){
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(matPath)));
            double[][] matrix=new double[domainChars.length()][domainChars.length()];
            Scanner sc=new Scanner(new File(matPath));
            String line=null;
            String[] line_split=null;
            int row=0;
            int col=0;
            while(sc.hasNext()){
                col=0;
                line=sc.nextLine();
                line_split=line.split("\t");
                for(String n:line_split){
                    matrix[row][col++] = Double.parseDouble(n);
                }
                row++;
            }
            return matrix;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void getTopDomain(){
        try {
            Scanner sc=new Scanner(ClassLoader.getSystemResourceAsStream("top-1m.csv"));
            int i=0;

            String outPath=new File("").getAbsolutePath()+"/src/main/resources/top-100000.txt";
            System.setOut(new PrintStream(new File(outPath)));
            while(i<100000){
                System.out.println(sc.nextLine().split("\\,",-1)[1].split("\\.",-1)[0]);
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    public static void saveTwoMatrix(){


           // saveMatrix("text.txt", new File("").getAbsolutePath()+"/src/main/resources/text_matrix.txt");
        saveMatrix("top-100000.txt", new File("").getAbsolutePath()+"/src/main/resources/top_matrix.txt");

    }
    public static void main(String[] args){

        saveTwoMatrix();
    }
}
