import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
class nGramTruple{
    String ngram=null;
    int start;
    int end;
    public nGramTruple(String ngram,int start,int end){
        this.ngram=ngram;
        this.start=start;
        this.end=end;
    }
}

public enum StringFeatures{
    INSTANCE;

    private int feaNum=32;  //features number, the length of vector
    private Map<Character,Integer> charIndex=null;  // the index of characters
    private Set<Character> VowelChar=null; // vowel
    Map<Integer,ArrayList<Integer>> TypeMap=null;
    private double[][] textMatrix=null;
    private double[][]  domainTopMatrix=null;
    HashSet<String> dictionary=null;
    private String ProjectPath= new File("").getAbsolutePath();

    private StringFeatures(){
        VowelChar=new HashSet<Character>();
        VowelChar.addAll(Arrays.asList('a','e','i','o','u'));
        TypeMap=new HashMap<Integer,ArrayList<Integer>>();
        for(int i=1;i<=5;i++)
        {
            TypeMap.put(i,new ArrayList<Integer>());
        }

        textMatrix=PairMatrix.getMatrix(ProjectPath+"/src/main/resources/text_matrix.txt");
        domainTopMatrix=PairMatrix.getMatrix(ProjectPath+"/src/main/resources/top_matrix.txt");

        String jsonText=null;
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(StringVector.class.getResourceAsStream("words.json")));
            jsonText=br.readLine();

        }catch(Exception e){
            e.printStackTrace();
        }
        List<String> words=JSON.parseArray(jsonText,java.lang.String.class);
        dictionary=new HashSet<String>(words);
    }

    private int getCharType(char a){
        if(a>='a' && a<='z') {
            if(VowelChar.contains(a))
                return 1;
            else
                return 2;
        }
        else if(a>'0' && a<'9'){
            return 3;
        }else if(a=='-'){
            return 4;
        }else{
            return 5;
        }
    }

    private double shannonEntropy(String domain){
        Map<Character,Double> charMap=new HashMap<Character,Double>();
        for(int i=0;i<domain.length();i++){
            Character c=domain.charAt(i);
            if(charMap.containsKey(c)){
                charMap.put(c,charMap.get(c)+1.0);
            }else{
                charMap.put(c,1.0);
            }
        }
        double entropy=0.0;
        for(Double d:charMap.values()){
            double pc=d/(domain.length()*1.0);
            entropy+=pc*(Math.log(pc)/Math.log(2));
        }
        return -entropy;
    }

    private int LingusticFeatures(String domain,double[] vector){
        // The Length of Subdomain
        int vectorIndex=0;
        vector[vectorIndex++]=domain.length();

        //  1 vowel; 2 consonant; 3 digit; 4 hyphen; 5 other;
        for(int i=1;i<=5;i++)
            TypeMap.get(i).clear();
        int preType=-1;
        int nowType;
        int counter=0;
        for(int i=0;i<domain.length();i++){
            nowType=getCharType(domain.charAt(i));
            if(nowType==preType){
                counter++;
            }else{
                if(preType!=-1){
                    TypeMap.get(preType).add(counter);
                    counter=1;
                }else{
                    counter++;
                }
            }
            preType=nowType;
        }
        TypeMap.get(preType).add(counter);

        for(int i=1;i<=3;i++){
            double total=0.0;
            double maxNum=-1.0;
            ArrayList<Integer> list=TypeMap.get(i);
            if(list.size()!=0){
                for(Integer c:list){
                    total+=c;
                    if(c>maxNum) maxNum=c*1.0;
                }
                //five features
                vector[vectorIndex++]=total;
                vector[vectorIndex++]=maxNum;
                vector[vectorIndex++]=total/list.size();
                vector[vectorIndex++]=maxNum/total;
                vector[vectorIndex++]=total/vector[0];
            }else{
                vectorIndex+=5;
            }
        }

        //Ratio of Hyphen
        double HyNum=0.0;
        for(Integer i:TypeMap.get(4)){
            HyNum+=i;
        }
        vector[vectorIndex++]=HyNum/vector[0];
        //Is a Hexadecimal Digit
        //Is a Decimal Digit
        int Hex=0;
        int Dec=0;
        for(int i=0;i<domain.length();i++){
            if(domain.charAt(i)>='0' && domain.charAt(i)<='9'){
                Hex++;
                Dec++;
            }
            if(domain.charAt(i)>='a' && domain.charAt(i)<='f'){
                Hex++;
            }
        }
        if(Hex==domain.length()){
            vector[vectorIndex++]=1.0;
        }
        else{
            vectorIndex++;
        }
        if(Dec==domain.length()){
            vector[vectorIndex++]=1.0;
        }else{
            vectorIndex++;
        }
        //Shannon Entropy
        vector[vectorIndex++]=shannonEntropy(domain);
        return vectorIndex;
    }


    private int transitionProbabilityFeatures(String domain,double[] vector,int vectorIndex){
        double logProbText=0.0;
        double logProbTopDomain=0.0;
        int num=0;
        for(num=0;num<domain.length()-1;num++){
            char a=domain.charAt(num);
            int ai=PairMatrix.charIndex.get(a);
            char b=domain.charAt(num+1);
            int bi=PairMatrix.charIndex.get(b);
            logProbText+=textMatrix[ai][bi];
            logProbTopDomain+=domainTopMatrix[ai][bi];
        }
        vector[vectorIndex++]= Math.exp(logProbText/num);
        vector[vectorIndex++]=Math.exp(logProbTopDomain/num);
        return vectorIndex;
    }


    private  List<nGramTruple> n_gram(String domain){
        if(domain==null || domain.length()==0) return null;
        List<nGramTruple> result=new ArrayList<nGramTruple>();
        int length=domain.length();
        for(int i=2;i<=length;i++){
            for(int j=0;j<=length-i;j++){
                result.add(new nGramTruple(domain.substring(j,j+i),j,j+i-1));
            }
        }

        return result;
    }

    private List<nGramTruple> checkWords(List<nGramTruple> nGram){
        List<nGramTruple> result=null;
        try{
            for(nGramTruple d:nGram)
                if(dictionary.contains(d.ngram)){
                    if(result==null){
                        result= new ArrayList<nGramTruple>();
                    }
                    result.add(d);
                }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private List<Integer> match(List<nGramTruple> words, int beginIndex){
        List<Integer> result=null;
        int minEndIndex=Integer.MAX_VALUE;
        int minLength=Integer.MIN_VALUE;
        for(int i=0;i<words.size();i++){
            nGramTruple w=words.get(i);
            if(w.start<=beginIndex){
                continue;
            }
            else{
                if(w.start>minEndIndex){
                    continue;
                }
                else{
                    if(result==null) result=new ArrayList<Integer>();
                    if(minEndIndex<w.end)
                        minEndIndex=w.end;
                    List<Integer> lastResult=match(words,w.end);

                    int length=w.end-w.start+1;
                    if(lastResult!=null){
                        for(Integer wi:lastResult){
                            length+=(words.get(wi).end-words.get(wi).start+1);
                        }
                    }
                    if(length>minLength){
                        result.clear();
                        result.add(i);
                        if(lastResult!=null){
                            for(Integer li:lastResult)
                                result.add(li);
                        }
                        minLength=length;
                    }else if(length == minLength){
                        if(lastResult==null){
                            result.clear();
                            result.add(i);
                            if(lastResult!=null){
                                for(Integer li:lastResult)
                                    result.add(li);
                            }
                        }
                        else if(lastResult.size()<result.size()-1){
                            result.clear();
                            result.add(i);
                            if(lastResult!=null){
                                for(Integer li:lastResult)
                                    result.add(li);
                            }
                        }

                    }
                }
            }
        }
        return result;
    }


    private int wordFeatures(String domain,double[] vector, int vectorIndex){
        if(domain==null || domain.length()==0) return 32;
        else if(domain.length()==1){

        }else{
            List<nGramTruple> nGram=n_gram(domain);
            List<nGramTruple> allWords=checkWords(nGram);

            if(allWords!=null){
                allWords.sort(new Comparator<nGramTruple>() {
                    @Override
                    public int compare(nGramTruple o1, nGramTruple o2) {
                        if(o2.start<o1.start){
                            return 1;
                        }else if(o2.start==o1.start){
                            if(o2.end>o1.end){
                                return 1;
                            }if (o2.end == o1.end){
                                return 0;
                            }else{
                                return -1;
                            }
                        }else{
                            return -1;
                        }
                    }
                });
                //allWords.forEach(a->System.out.println(a.ngram+" "+a.start+" "+a.end));
                List<Integer> matchIndexes=match(allWords,-1);
                //Number of Words in AllWords
                vector[vectorIndex++]=allWords.size();
                //Number of Maximum Matching Words
                vector[vectorIndex++]=matchIndexes.size();

                double maxLengthMMW=0.0;
                double totalLengthMMW=0.0;
                for(Integer mi:matchIndexes){
                    double mlength=allWords.get(mi).end-allWords.get(mi).start+1;
                    totalLengthMMW+=mlength;
                    if(mlength>maxLengthMMW) maxLengthMMW=mlength;
                }
                //Ratio of Length Summation of Maximum Matching Words to Length of Subdomain
                vector[vectorIndex++]=totalLengthMMW/domain.length();
                //Maximal Length of Words in Maximum Matching Words
                vector[vectorIndex++]=maxLengthMMW;
                //Mean Length of Words in Maximum Matching Words
                vector[vectorIndex++]=totalLengthMMW/matchIndexes.size();
                List<Integer> interval=null;
                int maxInterval=0;
                int minInterval=Integer.MAX_VALUE;
                int totalInterval=0;
                boolean onlyHyphen=true;
                if(matchIndexes.size()>1){
                    for(int i=0;i<matchIndexes.size()-1;i++){
                        if(allWords.get(i+1).start>(allWords.get(i).end+1)){
                            int intervalTemp=allWords.get(i+1).start-(allWords.get(i).end+1);
                            if(interval==null) interval=new ArrayList<Integer>();
                            interval.add(intervalTemp);
                            totalInterval+=intervalTemp;
                            if(intervalTemp>maxInterval) maxInterval=intervalTemp;
                            if(intervalTemp<minInterval) minInterval=intervalTemp;
                            if(onlyHyphen){
                                for(int ii=allWords.get(i).end+1;ii<allWords.get(i+1).start;ii++){
                                    if(domain.charAt(ii)!='-')
                                        onlyHyphen=false;
                                }
                            }
                        }
                    }
                }
                if(interval!=null){
                    //Number of IntervalWords
                    vector[vectorIndex++]=interval.size();
                    //Maximal Length of Word in IntervalWords
                    vector[vectorIndex++]=maxInterval;
                    //Minimal Length of Word in IntervalWords
                    vector[vectorIndex++]=minInterval;
                    //Mean Length of IntervalWords
                    vector[vectorIndex++]=totalInterval/interval.size();
                    //Is Only Hyphen in IntervalWords
                    if(onlyHyphen){
                        vector[vectorIndex++]=1.0;
                    }
                    else{
                        vector[vectorIndex++]=0.0;
                    }

                }else{
                    System.out.println("no interval words");
                }


            }else{
                System.out.println("not  any words");
            }
        }
        return 32;
    }

    public double[] getOneDomainFeatures(String domain){
        if(domain==null || domain.length()==0) return null;
        double[] vector=new double[feaNum];
        int index=0;
        domain=domain.toLowerCase();
        index=LingusticFeatures(domain,vector);
        System.out.print(index+" - ");
        index=transitionProbabilityFeatures(domain,vector,index);
        System.out.print(index+" - ");
        index=wordFeatures(domain,vector,index);
        System.out.println(index);
        return vector;
    }

    public static void main(String[] args){
        double[] v=StringFeatures.INSTANCE.getOneDomainFeatures("890-win-chester-man");
        for(int i=0;i<v.length;i++) System.out.print(v[i]+" ");
        System.out.println();
    }
}
