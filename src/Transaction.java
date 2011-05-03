import java.util.LinkedList;
import java.util.Set;

public class Transaction {

	LinkedList<String> items = new LinkedList<String>();

	public Transaction(String itemArr[], String titleAttrs[]) {
		// separator ":" as "." is very common in words
		for (int i = 0; i < itemArr.length; i++) {
			items.add(new String(titleAttrs[i] + "=" + itemArr[i]));
		}
	}

	public Transaction(String itemArr[]) {
		for (int i = 0; i < itemArr.length; i++) {
			items.add(new String(itemArr[i]));
		}
	}

	public boolean containsAll(Set<String> s) {
		return items.containsAll(s);
	}

}
