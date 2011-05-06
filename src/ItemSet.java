import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/*
 * ItemSet class
 */
public class ItemSet {

	// ItemSet is sorted according to the String items
	TreeSet<String> items = new TreeSet<String>();

	public ItemSet(LinkedList<String> items) {
		this.items.addAll(items);
	}

	public ItemSet(long bitmap, ItemSet is) {
		Object itemsp[] = is.items.toArray();
		for (int j = 0; j < is.items.size(); j++) {
			if ((bitmap & ((int) Math.pow(2, j))) != 0) {
				items.add((String) itemsp[j]);
			}
		}
	}
	
	public boolean equals(Object other){
		  if (other == null) return false;
		    if (other == this) return true;
		    if (this.getClass() != other.getClass())return false;
		    ItemSet is=(ItemSet)other;
		if(is.items.size()!=this.items.size())
			return false;
		if(is.items.containsAll(this.items))
		return true;
		
		return false;
		
	}

	public boolean intersectsOne(ItemSet is) {
		if ((this.items.size() == 1) && (is.items.size() == 1)
				&& this.items.first().compareTo(is.items.first()) < 0)
			return true;
		if ((this.items.size() == 1) && (is.items.size() == 1)
				&& this.items.first().compareTo(is.items.first()) >= 0)
			return false;
		int i = 0;
		Iterator<String> i1 = is.items.iterator();
		Iterator<String> i2 = this.items.iterator();

		while (i1.hasNext() && i2.hasNext()) {
			String s1 = i1.next();
			String s2 = i2.next();
			if (!s1.equals(s1 = s2))
				return false;
			i++;
			if (i == (is.items.size() - 1))
				break;
		}

		if (i1.next().compareTo(i2.next()) < 0)
			return true;
		return false;
	}

	public String nextString(ItemSet is) {

		Iterator<String> iterX = this.items.iterator();
		Iterator<String> iterY = is.items.iterator();
		int lenX = this.items.size();
		int lenY = is.items.size();

		if (lenX != lenY) {
			return null;
		} else {
			while (lenX > 1) {
				if (iterX.next() != iterY.next()) {
					return null;
				}
				lenX--;
			}
			String x = iterX.next();
			String y = iterY.next();
			if (x.compareTo(y) < 0)
				return y;
		}
		return null;
	}
}
