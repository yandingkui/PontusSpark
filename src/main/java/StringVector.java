import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringVector {


    class CharStatic{
        double total=0;
        double consecutive_num=0;
        List<Double> consecutive_array=null;
        CharStatic() {
            consecutive_array = new ArrayList<Double>();
        }
    }

    private Map<Character,Integer> charIndex=null;

    private char[] VowelChar={'a','e','i','o','u'};

    private enum CHARTYPE{VOWEL,CONSONANT,DIGIT,HYPHEN,OTHER};



    /**
     * Constructue
     *
     */

    StringVector(){
        charIndex=new HashMap<Character, Integer>();
        String domainChars="abcdefghijklmnopqrstuvwxyz1234567890-_";
        for(int i=0;i<domainChars.length();i++)
            charIndex.put(domainChars.charAt(i),i);
    }

    /**
     *
     * @param c
     * @return type of a character.
     */
    private CHARTYPE type(char c){
        if(c>='a' && c<='z'){
            for(char v: VowelChar){
                if(c==v) return CHARTYPE.VOWEL;
            }
            return CHARTYPE.CONSONANT;
        }
        if(c>='0' && c<='9') return CHARTYPE.DIGIT;
        if(c=='-') return CHARTYPE.HYPHEN;
        return CHARTYPE.OTHER;
    }

    /**
     *
     * @param now
     * @param pre
     * @param FourTypeStatic
     */
    private void charTypeStatic(CHARTYPE now,CHARTYPE pre,Map<CHARTYPE,CharStatic> FourTypeStatic){
        if(now==null || pre==null || FourTypeStatic==null) return;
        if(now==pre){
            CharStatic temp=FourTypeStatic.get(now);
            temp.consecutive_num++;
            temp.total++;
        }else{
            CHARTYPE key;
            CharStatic value;
            for(Map.Entry<CHARTYPE,CharStatic> entry:FourTypeStatic.entrySet()){
                key=entry.getKey();
                value=entry.getValue();
                if(key==now){
                    value.consecutive_num++;
                    entry.getValue().total++;
                }
                else{
                    if(value.consecutive_num!=0){ 
                        value.consecutive_array.add(value.consecutive_num);
                        value.consecutive_num=0;
                    }
                }
            }
        }
    }

    /**
     * obtain the linguistic features
     * @param domain
     * @param vector
     *
     */
    private void setLinguisticFeatures(String domain,List<Double> vector){
        if(vector==null || Strings.isNullOrEmpty(domain)) return;

        Map<CHARTYPE,CharStatic> FourTypeStatic=new HashMap<CHARTYPE, CharStatic>();
        FourTypeStatic.put(CHARTYPE.VOWEL,new CharStatic());
        FourTypeStatic.put(CHARTYPE.CONSONANT,new CharStatic());
        FourTypeStatic.put(CHARTYPE.DIGIT,new CharStatic());
        FourTypeStatic.put(CHARTYPE.HYPHEN,new CharStatic());

        CHARTYPE preType=null;
        CHARTYPE nowType=null;
        int length=domain.length();
        for(int i=0;i<length;i++){
            char c=domain.charAt(i);
            nowType=type(c);
            charTypeStatic(nowType,preType,FourTypeStatic);
            if(i==0){
                preType=type(c);
                FourTypeStatic.get(preType).consecutive_num++;
                FourTypeStatic.get(preType).total++;
            }
            if(i==length-1){
                FourTypeStatic.get(nowType).consecutive_array.add(FourTypeStatic.get(nowType).consecutive_num);
            }
            preType=nowType;
        }

//        for(Map.Entry<CHARTYPE,CharStatic> entry:FourTypeStatic.entrySet()){
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue().total +" "+ entry.getValue().consecutive_num);
//            System.out.println(entry.getValue().consecutive_array);
//        }
    }

    /**
     *
     * @param domains
     * @return
     */
    public List<List<Double>> getFeatures(List<String> domains){
        List<List<Double>> result= new ArrayList<List<Double>>(domains.size());
        for(String d:domains){
            List<Double> vector=new ArrayList<Double>(32);
            //length
            vector.add(d.length()*1.0);
        }
        return null;
    }

    public static void main(String[] args){

    }
}