package jp.sample;

import jp.sample.ano.SampleAnotetion;

public class AnoSample {
	public static void main(String[] args){
		AnoSample my = new AnoSample();
		my.print("AnoSample");
	}
	
	
	@SampleAnotetion("hogeMapping")
	public void print(String message){
		System.out.println(message);
	}
}