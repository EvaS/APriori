import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class ItemSet {

	TreeSet<String> items = new TreeSet<String>();

	public ItemSet(LinkedList<String> items) {
		this.items.addAll(items);
	}
	
	public ItemSet(int bitmap, ItemSet is) {
		Object itemsp[]=is.items.toArray();
		for(int j=0;j<is.items.size();j++){
			if((bitmap & ((int)Math.pow(2,j)))!=0){
				items.add((String)itemsp[j]);
			}
		}
	}

	public boolean intersectsOne(ItemSet is) {
		if((this.items.size()==1) && (is.items.size()==1) && this.items.first().compareTo(is.items.first())<0)
			return true;
		if((this.items.size()==1) && (is.items.size()==1) && this.items.first().compareTo(is.items.first())>=0)
			return false;
		int i=0;
		Iterator<String> i1= is.items.iterator();
		Iterator<String> i2= this.items.iterator();

		while(i1.hasNext() && i2.hasNext()){
			String s1=i1.next();
			String s2=i2.next();
			if(!s1.equals(s1=s2))
				return false;
			i++;
			if(i==(is.items.size()-1))
				break;
		}

		if(i1.next().compareTo(i2.next())<0)
			return true;
		return false;
	}

}
