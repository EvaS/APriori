import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class APriori {
	
	public void mineFreqSets(double minConf,double minSupp,DataSet d){
		
	}
	
	public static void main(String args[])
	{
		APriori apriori=new APriori();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter name of dataset file: ");
			String fileName=br.readLine();
			DataSet d=new DataSet(fileName);
			double minSupp,minConf;
			System.out.print("Enter minimum support: ");
			minSupp=Double.parseDouble(br.readLine());
			System.out.print("Enter minimum confidence: ");
			minConf=Double.parseDouble(br.readLine());
			apriori.mineFreqSets(minConf, minSupp,d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
