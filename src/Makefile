JCC = javac

JFLAGS = -g

CLASSPATH = .

default: APriori.class DataSet.class Transaction.class ItemSet.class

APriori.class: DataSet.java Transaction.java ItemSet.java APriori.java
	$(JCC) $(JFLAGS) -cp . ./APriori.java

DataSet.class: DataSet.java
	$(JCC) $(JFLAGS) DataSet.java

Transaction.class: Transaction.java
	$(JCC) $(JFLAGS) Transaction.java

ItemSet.class: ItemSet.java
	$(JCC) $(JFLAGS) ItemSet.java

clean: 
	rm *.class output.txt top_output.txt
