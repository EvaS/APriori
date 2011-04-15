import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class DataSet {

	String titleAttrs[];
	LinkedList<ArrayList> dRows=new LinkedList<ArrayList>();
	
	public DataSet(String fileName) {
		String dLine;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String title=br.readLine();
			titleAttrs=title.split(",");
			while ((dLine = br.readLine()) != null) {
				//System.out.println(dLine);
				String values[]=dLine.split(",");
				//dRows.add();
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}
}
