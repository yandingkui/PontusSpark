public class SparkTrain {
//    public static void main(String[] args){
//        SparkConf conf = new SparkConf().setAppName("pontus");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        JavaRDD<String> textFile = sc.textFile("hdfs://...");
//        JavaPairRDD<String, Integer> counts = textFile
//                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
//                .mapToPair(word -> new Tuple2<>(word, 1))
//                .reduceByKey((a, b) -> a + b);
//
//    }
    public static void main(String[] args){
        System.out.println("hello");
    }

}
