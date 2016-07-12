import java.util.*;

import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.unimi.it.data.Certificate;



public class PartialAuction {
	private PartiallyOrderedSet<Set<Certificate>> poset;
	// Ratio between bids and bidders
	private static final int BID_RATE = 10; // a bidder every 10 bids
	// Number of roots of bids for each bidder
	private static final int ROOTS = 2;
	private int nbids;
	private int nbidders;
	private ArrayList<Set<Certificate>> bids;
	// b in betterRel.get(a) iff b is (directly) better than a
	private Map<Certificate,Set<Certificate>> betterRel;
	private Map<Certificate,Set<Certificate>> worseRel;
	private Map<Certificate,Set<Integer>> owners;
		
	public PartialAuction(PartiallyOrderedSet<Set<Certificate>> poset)
	{
		this.poset = poset;
		setup();
	}

	private void setup()
	{		
		// System.out.println("size: "+ n + ", bidders: " + nbidders);
		ArrayList<Certificate> universe = new ArrayList<Certificate>();
		
		// Collect all bids in universe
		for (Set<Certificate> s: poset) {
			for (Certificate c: s) { 
				universe.add(c);
				// System.out.print(c.getProperties() + "; ");
			}			
			// System.out.println("---");
		}
		
		this.nbids = universe.size();
		this.nbidders = nbids / BID_RATE;
		if (nbidders<2) nbidders = 2;
		this.bids = new ArrayList<Set<Certificate>>(nbidders);
		
		// Obtain a flat "better than" relation
		this.betterRel = flattenMap(poset.getGreaterRelations());
		this.worseRel = flattenMap(poset.getLesserRelations());
	
		this.owners = new HashMap<Certificate,Set<Integer>>();
		for (Certificate c: universe)
			owners.put(c,  new HashSet<Integer>());
		
		// DEBUG
		// printFlatMap(betterRel);
		
		// Establish bids for each bidder
		
		// First, add ROOTS random subtrees
		for (int i=0; i<nbidders; i++) {
			Set<Certificate> ibids = new HashSet<Certificate>();
			bids.add(ibids);
			for (int j=0; j<ROOTS; j++) {
				int root = (int) (Math.random() * nbids);
				addSubTree(ibids, universe.get(root), worseRel, i);
			}
			
		}
		// Then, assign each unassigned item to some random bidder
		for (Certificate c: universe) {
			if (owners.get(c).isEmpty()) {
				// DEBUG
				// System.out.println(c + "not assigned!");
				int owner = (int) (Math.random() * nbidders);
				bids.get(owner).add(c);
				owners.get(c).add(owner);
			}
		}
		// DEBUG
		// printBids();
	}
	
	/* Adds to s the subtree rooted in x in the tree t
	 * (t is the father-children relation)
	 */
	private <T> void addSubTree(Set<T> s, T x, Map<T,Set<T>> t, int index)
	{
		s.add(x);
		owners.get(x).add(index);
		Set<T> children = t.get(x);
		if (children==null) return;
		for (T y: children)
			addSubTree(s, y, t, index);
	}
	
	private void printBids()
	{
		int i = 0;
		for (Set<Certificate> s: bids) {
			System.out.print(i++ + ": ");
			for (Certificate c: s)
				System.out.print("(" + c.getId() + ":" + c.getProperty("PRIMA") + ") ");
			System.out.println();
		}
	}
	
	private void printFlatMap(Map<Certificate,Set<Certificate>> m)
	{
		for (Certificate c: m.keySet()) {
			System.out.println("(" + c.getId() + ") " + c.getProperties().get("PRIMA") + ":");
			for (Certificate c2: m.get(c))
				System.out.println("\t (" + c2.getId() + ") " + c2.getProperties().get("PRIMA"));
			System.out.println();
		}
	}
	
	/* The returned map is guaranteed to associate a (possibly empty) value to each item
	   occurring in one of the keys of m.
	 */
	private <T> Map<T,Set<T>> flattenMap(Map<Set<T>,HashSet<Set<T>>> m)
	{
		Map<T,Set<T>> flatMap = new HashMap<T,Set<T>>();
		
		for (Set<T> s1: m.keySet()) {
			Set<Set<T>> value = m.get(s1);
			Set<T> flatValue = new HashSet<T>();
			for (Set<T> s2: value)
				for (T x: s2)
					flatValue.add(x);
			for (T x: s1) {
				flatMap.put(x, flatValue);
			}
		}
		return flatMap;
	}
	
	/* Recursively determines if c is dominated (worse according to relation betterRel)
	 * by some other bid of another bidder (i.e., a bidder different from c_owner).
	 * 
	 * It assumes that the partial order betterRel is a tree.
	 */
	private boolean isDominated(Certificate c, int c_owner)
	{
		Set<Certificate> better = betterRel.get(c);
		if (better==null || better.isEmpty()) return false;
		for (Certificate d: better) {
			Set<Integer> d_owners = owners.get(d);
			for (Integer i: d_owners)
				if (i != c_owner) return true;
			if (isDominated(d, c_owner)) return true;
		}			
		return false;
	}
	
	// Main method
	public int getWinner() 
	{
		final ArrayList<Set<Certificate>> filter = new ArrayList<Set<Certificate>>(nbidders);
		// Candidate winners
		final Set<Integer> cw = new HashSet<Integer>();
		int winner = -1;
	
		// Compute "filter" of each bidder
		for (int i=0; i<nbidders; i++) {
			// System.out.print(i + ": ");
			Set<Certificate> ibids = bids.get(i);
			Set<Certificate> ifilter = new HashSet<Certificate>();
			filter.add(ifilter);
			for (Certificate c1: ibids) {
				// System.out.print(" (" + c1.getId() + ")");
				if (!isDominated(c1, i)) {
					ifilter.add(c1);
					cw.add(i);
					// System.out.print("*");
				}
			}
			// System.out.println();
		}
	
		// DEBUG
		//System.out.println("\nCandidate Winners: " + cw);

		if (!cw.isEmpty())
			winner = cw.iterator().next(); // there's no point in actually randomizing here
		return winner;
	}
}
