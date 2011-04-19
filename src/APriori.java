import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class APriori {

	private DataSet dataset;
	private double minSupp;
	private double minConf;
	TreeMap<Double, LinkedList<ItemSet>> frequent = new TreeMap<Double, LinkedList<ItemSet>>();
	TreeMap<Double, LinkedList<HashMap<ItemSet, ItemSet>>> confident = new TreeMap<Double, LinkedList<HashMap<ItemSet, ItemSet>>>();
	private String FILENAME = "output.txt";

	public APriori(DataSet d, double minSupp, double minConf) {
		this.dataset = d;
		this.minSupp = minSupp;
		this.minConf = minConf;
	}

	public HashMap<ItemSet, Integer> generateL1() {

		HashMap<String, Integer> singles = new HashMap<String, Integer>();
		HashMap<ItemSet, Integer> itemsL1 = new HashMap<ItemSet, Integer>();

		LinkedList<Transaction> transactions = this.dataset.getTranscations();
		for (Transaction t : transactions) {
			for (String item : t.items) {
				if (singles.containsKey(item)) {
					Integer freq = singles.get(item);
					singles.put(item, ++freq);
				} else
					singles.put(item, 1);
			}
		}
		for (String s : singles.keySet()) {
			double supp = (singles.get(s) / (dataset.size() * 1.0));
			if ((singles.get(s) / (dataset.size() * 1.0)) >= this.minSupp) {

				LinkedList<String> tl = new LinkedList<String>();
				tl.add(s);
				ItemSet is = new ItemSet(tl);

				itemsL1.put(is, singles.get(s));
				LinkedList<ItemSet> items = frequent.get(supp);
				if (items != null) {
					items.add(is);
				} else {
					items = new LinkedList<ItemSet>();
					items.add(is);
					frequent.put(supp, items);
				}
			}
		}

		return itemsL1;
	}

	public void mineFreqSets() {
		HashMap<ItemSet, Integer> lk;
		LinkedList<ItemSet> c1, c2;
		lk = this.generateL1();

		while (!lk.isEmpty()) {
			c1 = new LinkedList<ItemSet>();
			c2 = new LinkedList<ItemSet>();
			c1.addAll(lk.keySet());
			c2.addAll(lk.keySet());
			lk = new HashMap<ItemSet, Integer>();
			for (ItemSet i1 : c1) {
				for (ItemSet i2 : c2) {

					if (i1.intersectsOne(i2)) {
						LinkedList<String> tl = new LinkedList<String>();
						tl.addAll(i1.items);
						tl.addAll(i2.items);
						ItemSet c = new ItemSet(tl);
						int support = this.dataset.getSupport(c);

						double suppcent = (support / (dataset.size() * 1.0));
						if (suppcent >= this.minSupp) {
							lk.put(c, support);
							LinkedList<ItemSet> items = frequent.get(suppcent);
							if (items != null) {
								items.add(c);
							} else {
								items = new LinkedList<ItemSet>();
								items.add(c);
								frequent.put(support / (dataset.size() * 1.0),
										items);
							}
						}
					}
				}
			}
		}

	}

	public void generateRules() {

		for (Double supp : frequent.descendingKeySet()) {
			LinkedList<ItemSet> lis = frequent.get(supp);
			for (ItemSet is : lis) {
				for (int i = 1; i <= Math.pow(2, is.items.size()); i++) {
					ItemSet s = new ItemSet(i, is);
					ItemSet s2 = new ItemSet(~i, is);
					if (s.items.isEmpty() || s2.items.isEmpty())
						continue;
					int supp1 = this.dataset.getSupport(is);
					int supp2 = this.dataset.getSupport(s);

					Double conf = supp1 / (1.0 * supp2);
					if (conf >= this.minConf) {
						HashMap<ItemSet, ItemSet> ii = new HashMap<ItemSet, ItemSet>();
						ii.put(s, s2);
						if (!this.confident.containsKey(conf)) {
							this.confident
									.put(
											conf,
											new LinkedList<HashMap<ItemSet, ItemSet>>());
						}
						this.confident.get(conf).add(ii);
					}
				}
			}
		}

	}

	public void outputRules() {

		FileWriter fstream;
		BufferedWriter out;
		try {
			fstream = new FileWriter(this.FILENAME);
			out = new BufferedWriter(fstream);
			out.write("==Large itemsets (min_sup:" + this.minSupp * 100 + "%)\n");
			for (Double supp : frequent.descendingKeySet()) {
				LinkedList<ItemSet> lis = frequent.get(supp);
				for (ItemSet is : lis) {
					this.writeItemSet(is, out);
					out.write(supp * 100 + " % \n");
				}
			}
			out.write("\n==High-confidence association rules (min_conf:" + this.minConf
					* 100 + "%)\n");
			for (Double conf : this.confident.keySet()) {
				for (HashMap<ItemSet, ItemSet> ii : this.confident.get(conf)) {
					for (ItemSet s1 : ii.keySet()) {
						ItemSet s=new ItemSet(new LinkedList<String>(s1.items));
						this.writeItemSet(s1, out);
						out.write("=>");
						ItemSet s2=ii.get(s1);
						this.writeItemSet(s2, out);
						s.items.addAll(s2.items);
						double supp=this.dataset.getSupport(s)/(dataset.size()*1.0);
						out.write("(Conf:" + conf * 100 + "%, Supp:"+supp*100 + "%)\n");
					}
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Prints an itemset
	 */
	public void printItemSet(ItemSet is) {

		System.out.println("[");
		for (String s : is.items) {
			System.out.println(s);
			if (!s.equals(is.items.last()))
				System.out.println(", ");
		}
		System.out.println("] ");

	}

	/**
	 * Writes an itemset to file
	 */
	public void writeItemSet(ItemSet is, BufferedWriter out) {
		try {
			out.write("[");
			for (String s : is.items) {
				out.write(s);
				if (!s.equals(is.items.last()))
					out.write(", ");
			}
			out.write("] ");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Main class calling a-priori algorithm
	 */
	public static void main(String args[]) {
		APriori apriori;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter name of dataset file: ");
			String fileName = br.readLine();
			DataSet d = new DataSet(fileName,true);

			double minSupp, minConf;

			System.out.print("Enter minimum support: ");
			minSupp = Double.parseDouble(br.readLine());

			System.out.print("Enter minimum confidence: ");
			minConf = Double.parseDouble(br.readLine());

			apriori = new APriori(d, minSupp, minConf);

			apriori.mineFreqSets();
			apriori.generateRules();
			apriori.outputRules();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
