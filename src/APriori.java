import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class APriori {

	private DataSet dataset;
	private double minSupp;
	private double minConf;
	TreeMap<Double, LinkedList<ItemSet>> frequent = new TreeMap<Double, LinkedList<ItemSet>>();
	TreeMap<Double, HashMap<ItemSet, ItemSet>> confident = new TreeMap<Double, HashMap<ItemSet, ItemSet>>();
	private String FILENAME = "output.txt";

	public APriori(DataSet d, double minSupp, double minConf) {
		this.dataset = d;
		this.minSupp = minSupp;
		this.minConf = minConf;
	}

	public Map<ItemSet, Integer> generateL1() {

		// each individual item with its corresponding frequency
		Map<String, Integer> singles = new TreeMap<String, Integer>();
		// single item sets, LinkedHashMap maintains the insertion order of keys
		Map<ItemSet, Integer> itemsL1 = new LinkedHashMap<ItemSet, Integer>();

		LinkedList<Transaction> transactions = this.dataset.getTransactions();
		for (Transaction t : transactions) {
			for (String item : t.items) {
				if (singles.containsKey(item)) {
					Integer freq = singles.get(item);
					singles.put(item, ++freq);
				} else {
					singles.put(item, 1);
				}
			}
		}

		for (String s : singles.keySet()) {
			double supp = singles.get(s) / (dataset.size() * 1.0);
			if (supp >= this.minSupp) {
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

	/**
	 * @author - eva
	 */
	public void mineFreqSets() {
		// initial itemset
		Map<ItemSet, Integer> lk;
		// for intersection between itemsets
		LinkedList<ItemSet> c1, c2;
		lk = this.generateL1();

		while (!lk.isEmpty()) {
			c1 = new LinkedList<ItemSet>();
			c2 = new LinkedList<ItemSet>();

			c1.addAll(lk.keySet());
			c2.addAll(lk.keySet());
			// reinitialize lk for k+1 round
			lk = new HashMap<ItemSet, Integer>();
			for (ItemSet i1 : c1) {
				for (ItemSet i2 : c2) {
					if (i1.intersectsOne(i2)) {
						LinkedList<String> tl = new LinkedList<String>();
						tl.addAll(i1.items);
						tl.addAll(i2.items);
						ItemSet c = new ItemSet(tl);
						int support = this.dataset.getSupport(c);
						double suppcent = support / (dataset.size() * 1.0);
						if (suppcent >= this.minSupp) {
							lk.put(c, support);
							LinkedList<ItemSet> items = frequent.get(suppcent);
							if (items != null) {
								items.add(c);
							} else {
								items = new LinkedList<ItemSet>();
								items.add(c);
								frequent.put(suppcent, items);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @author : aman
	 */

	public void mineFrequentItemSets() {
		// initial item sets
		Map<ItemSet, Integer> itemsMap;
		// for intersection between item sets
		List<ItemSet> preCand = new LinkedList<ItemSet>();
		itemsMap = this.generateL1();

		while (!itemsMap.isEmpty()) {
			preCand.clear();
			preCand.addAll(itemsMap.keySet());
			// get the size of all previous candidates
			int length = preCand.size();
			// reinitialize itemsMap for next round
			itemsMap = new HashMap<ItemSet, Integer>();
			for (int i = 0; i < length; i++) {
				for (int j = i + 1; j < length; j++) {
					ItemSet i1 = preCand.get(i);
					ItemSet i2 = preCand.get(j);
					String next = i1.nextString(i2);
					if (next != null) {
						LinkedList<String> tl = new LinkedList<String>();
						tl.addAll(i1.items);
						tl.add(next);
						ItemSet c = new ItemSet(tl);
						int support = this.dataset.getSupport(c);
						double suppcent = support / (dataset.size() * 1.0);
						if (suppcent >= this.minSupp) {
							itemsMap.put(c, support);
							LinkedList<ItemSet> items = frequent.get(suppcent);
							if (items != null) {
								items.add(c);
							} else {
								items = new LinkedList<ItemSet>();
								items.add(c);
								frequent.put(suppcent, items);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * R: l => r ; l,r E T
	 */
	public void generateRules() {

		for (Double supp : frequent.descendingKeySet()) {
			LinkedList<ItemSet> lis = frequent.get(supp);
			for (ItemSet is : lis) {
				// get all the subsets of set {2^n}
				for (long i = 1; i <= Math.pow(2, is.items.size()); i++) {
					// LHS
					ItemSet l = new ItemSet(i, is);
					// RHS
					ItemSet r = new ItemSet(~i, is);
				
					//Compute only rules where the right side has one item
					if (l.items.isEmpty()|| r.items.size()>1 || r.items.isEmpty())
						continue;
					int suppLR = this.dataset.getSupport(is);
					int suppL = this.dataset.getSupport(l);

					Double conf = suppLR / (1.0 * suppL);
					if (conf >= this.minConf ) {
						if (!this.confident.containsKey(conf)) {
							this.confident.put(conf,
									new HashMap<ItemSet, ItemSet>());
						}
						this.confident.get(conf).put(l, r);
					}
				}
			}
		}
	}

	public void outputRules() {

		FileWriter fstream;
		BufferedWriter br;
		PrintWriter out;
		try {
			fstream = new FileWriter(this.FILENAME);
			br = new BufferedWriter(fstream);
			out = new PrintWriter(br);
			out.write("==Large itemsets (min_sup:" + this.minSupp * 100
					+ "%)\n");
			for (Double supp : frequent.descendingKeySet()) {
				LinkedList<ItemSet> lis = frequent.get(supp);
				for (ItemSet is : lis) {
					this.writeItemSet(is, out);
					out.printf("%.2f %% \n", supp * 100);
				}
			}
			out.write("\n==High-confidence association rules (min_conf:"
					+ this.minConf * 100 + "%)\n");
			for (Double conf : this.confident.descendingKeySet()) {
				for (ItemSet l : this.confident.get(conf).keySet()) {
					// LHS
					this.writeItemSet(l, out);
					out.write("=>");
					// RHS
					ItemSet r = this.confident.get(conf).get(l);
					this.writeItemSet(r, out);
					// LHS U RHS
					ItemSet is = new ItemSet(new LinkedList<String>(l.items));
					is.items.addAll(r.items);
					double supp = this.dataset.getSupport(is)
							/ (dataset.size() * 1.0);
					out.printf("Conf: %.2f %% , Supp: %.2f %% \n", conf * 100,
							supp * 100);
				}
			}
			out.close();
			System.out
					.println("Output written to disk, check output.txt file :)");
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
	public void writeItemSet(ItemSet is, PrintWriter out) {
		out.write("[");
		for (String s : is.items) {
			out.write(s);
			if (!s.equals(is.items.last()))
				out.write(", ");
		}
		out.write("] ");
	}

	/*
	 * Main class calling a-priori algorithm Usage: $java APriori <datafile>
	 * <min_support> <min_confidence>
	 */

	public static void main(String args[]) {
		APriori apriori;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Enter name of dataset file: ");
			String fileName = br.readLine().trim();
			DataSet d = new DataSet(fileName, true);

			double minSupp, minConf;

			System.out.print("Enter minimum support: ");
			minSupp = Double.parseDouble(br.readLine());

			System.out.print("Enter minimum confidence: ");
			minConf = Double.parseDouble(br.readLine());

			apriori = new APriori(d, minSupp, minConf);

			// apriori.mineFreqSets();
			apriori.mineFrequentItemSets();
			apriori.generateRules();
			apriori.outputRules();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// for final submission
		/*
		 * if (args.length >= 3) {
		 * 
		 * String fileName = args[0]; double minSupp, minConf; minSupp =
		 * Double.parseDouble(args[1]); minConf = Double.parseDouble(args[2]);
		 * 
		 * DataSet d = new DataSet(fileName, true); apriori = new APriori(d,
		 * minSupp, minConf); apriori.mineFreqSets(); apriori.generateRules();
		 * apriori.outputRules(); }
		 */
	}
}
