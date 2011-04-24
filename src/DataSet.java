import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class DataSet {

	String titleAttrs[];
	// list of all transactions objects
	LinkedList<Transaction> dRows = new LinkedList<Transaction>();
	// map of <itemSet, support> 
	HashMap<Set<String>, Integer> support = new HashMap<Set<String>, Integer>();

	public DataSet(String fileName, boolean hasTitle) {
		String dLine;
		String LOCALROOT = "./res/";
		try {
			FileInputStream fstream = new FileInputStream(LOCALROOT + fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String title = br.readLine();
			if (hasTitle) {
				titleAttrs = title.split(",");
			} else {
				dRows.add(new Transaction(title.split(",")));
			}
			while ((dLine = br.readLine()) != null) {
				Transaction tr;
				if (hasTitle)
					tr = new Transaction(dLine.split(","), titleAttrs);
				else
					tr = new Transaction(dLine.split(","));
				dRows.add(tr);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error creating transaction dataset :(");
			e.printStackTrace();
		}
	}

	LinkedList<Transaction> getTransactions() {
		return dRows;
	}

	public int size() {
		return dRows.size();
	}

	public int getSupport(ItemSet is) {
		if (this.support.get(is.items) == null) {
			int support = 0;
			for (Transaction t : this.dRows) {
				if (t.containsAll(is.items))
					support++;
			}
			this.support.put(is.items, support);
			return support;
		}
		return this.support.get(is.items);
	}
}
