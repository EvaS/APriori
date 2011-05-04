import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Main class implementing APriori algorithm
 */
public class APriori {

	private DataSet dataset;
	private double minSupp;
	private double minConf;
	TreeMap<Double, LinkedList<ItemSet>> frequent = new TreeMap<Double, LinkedList<ItemSet>>();
	TreeMap<Double, HashMap<ItemSet, ItemSet>> confident = new TreeMap<Double, HashMap<ItemSet, ItemSet>>();
	private String FILENAME = "output.txt"; // Filename where results of rules
											// are stored

	/*
	 * Constructor of APriori
	 */
	public APriori(DataSet d, double minSupp, double minConf) {
		this.dataset = d;
		this.minSupp = minSupp;
		this.minConf = minConf;
	}

	/*
	 * Generates L1 set of dataset
	 */
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

	/*
	 * Alternative implementation of second step of APriori algorithm
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

	/*
	 * Second step of APriori algorithm
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
			itemsMap = new LinkedHashMap<ItemSet, Integer>();
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

	/*
	 * Generates rules of high confidence (>min_supp) R: l => r ; l,r E T
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

					// Generate rules with RHS having one item
					if (l.items.isEmpty() || r.items.isEmpty()
							|| r.items.size() > 1)
						continue;
					int suppLR = this.dataset.getSupport(is);
					int suppL = this.dataset.getSupport(l);

					Double conf = suppLR / (1.0 * suppL);
					if (conf >= this.minConf) {
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

	/*
	 * Saves the rules to output file
	 */
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

	/*
	 * Outputs top-5 rules for each conclusion to top_ouput file
	 */
	public void outputTopRules() {

		FileWriter fstream;
		BufferedWriter br;
		PrintWriter out;
		HashMap<String, Integer> cntRules = new HashMap<String, Integer>();
		try {
			fstream = new FileWriter("top_" + this.FILENAME);
			br = new BufferedWriter(fstream);
			out = new PrintWriter(br);

			out.write("==High-confidence association rules (min_conf:"
					+ this.minConf * 100 + "%)\n");
			for (Double conf : this.confident.descendingKeySet()) {
				for (ItemSet l : this.confident.get(conf).keySet()) {
					ItemSet r = this.confident.get(conf).get(l);
					String conc = r.items.first();
					if (cntRules.containsKey(conc)) {
						int p = cntRules.get(conc);
						if (p >= 5)
							continue;
						cntRules.put(conc, p + 1);
					} else {
						cntRules.put(conc, 1);
					}
					// LHS
					this.writeItemSet(l, out);
					out.write("=>");
					// RHS
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
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

	/*
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

		if (args.length < 3) {
			System.err.println("Wrong number of arguments!");
			System.err.println("Usage: <Dataset> <min_sup> <min_conf>");
			System.exit(-1);
		}

		String fileName = args[0];
		// Second boolean argument true because we have titled dataset
		DataSet d = new DataSet(fileName, true);

		double minSupp, minConf;

		minSupp = Double.parseDouble(args[1]);
		minConf = Double.parseDouble(args[2]);

		apriori = new APriori(d, minSupp, minConf);

		// apriori.mineFreqSets();
		apriori.mineFrequentItemSets();
		apriori.generateRules();
		apriori.outputRules();
		apriori.outputTopRules();

	}
}
