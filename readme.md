javac jp\sample\ano\SampleAnotetion.java jp\sample\AnoSample.java jp\sample\AnoSample2.java
jar cvf anosample.jar .\jp

java -cp anosample.jar jp.sample.AnoSample
java -cp anosample.jar jp.sample.AnoSample2

javac -cp anosample.jar WebMappingLoader.java
jar cvf webmappingloader.jar WebMappingLoader.class

java -cp "webmappingloader.jar;anosample.jar" WebMappingLoader
#java -cp ".;anosample.jar" WebMappingLoader

