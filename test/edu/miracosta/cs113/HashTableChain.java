package edu.miracosta.cs113;
import java.util.*;

/**
 * HashTable implementation using chaining to tack a pair of key and value pairs.
 * @param <K> Generic Key
 * @param <V> Generic Value
 */
public class HashTableChain<K, V> implements Map<K, V>  {

    private LinkedList<Entry<K, V>>[] table ;
    private  int numKeys ;
    private static final int CAPACITY = 101 ;
    private static final double LOAD_THRESHOLD = 1.5 ;

    ///////////// ENTRY CLASS ///////////////////////////////////////

    /**
     * Contains key-value pairs for HashTable
     * @param <K> the key
     * @param <V> the value
     */
    private static class Entry<K, V> implements Map.Entry<K, V>{
        private K key ;
        private V value ;

        /**
         * Creates a new key-value pair
         * @param key the key
         * @param value the value
         */
        public Entry(K key, V value) {
            this.key = key ;
            this.value = value ;
        }

        /**
         * Returns the key
         * @return the key
         */
        public K getKey() {
            return  key;
        }

        /**
         * Returns the value
         * @return the value
         */
        public V getValue() {
            return value ;
        }

        /**
         * Sets the value
         * @param val the new value
         * @return the old value
         */
        public V setValue(V val) {
            V oldVal = value;
            value = val ;
            return oldVal ;
        }
        @Override
        public String toString() {
            return  key + "=" + value  ;
        }



    }

    ////////////// end Entry Class /////////////////////////////////

    ////////////// EntrySet Class //////////////////////////////////

    /**
     * Inner class to implement set view
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {


        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new SetIterator();
        }

        @Override
        public int size() {
            return numKeys ;
        }
    }

    ////////////// end EntrySet Class //////////////////////////////

    //////////////   SetIterator Class ////////////////////////////

    /**
     * Class that iterates over the table. Index is table location
     * and lastItemReturned is entry
     */
    private class SetIterator implements Iterator<Map.Entry<K, V>> {

        private int index = 0 ;
        private Entry<K,V> lastItemReturned = null;
        private Iterator<Entry<K, V>> iter = null;

        @Override
        public boolean hasNext() 
        {
        	if (iter != null && iter.hasNext()) 
        	{
                return true;
            }
            do 
            {
                index++;
                if (index >= table.length) 
                {
                    return false;
                }
            } while (table[index] == null);
            iter = table[index].iterator();
            return iter.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
        	if (iter.hasNext()) 
        	{
                lastItemReturned = iter.next();
                return lastItemReturned;
            } 
        	
        	else 
            {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
        	if (lastItemReturned == null)
        	{
        		throw new IllegalStateException();
        	}
        	
        	else
        	{
        		iter.remove();
        		lastItemReturned = null;
        	}
        }
    }

    ////////////// end SetIterator Class ////////////////////////////

    /**
     * Default constructor, sets the table to initial capacity size
     */
    public HashTableChain() {
        table = new LinkedList[CAPACITY] ;
    }

    // returns number of keys
    @Override
    public int size() {
        return numKeys;
    }

    // returns boolean if table has no keys
    @Override
    public boolean isEmpty() {
    	return numKeys == 0;
    }

    // returns boolean if table has the searched for key
    @Override
    public boolean containsKey(Object key) {
    	int index = key.hashCode() % table.length;
    	
    	if (index < 0)
    	{
    		index += table.length;
    	}
    	
    	if (table[index] == null)
    	{
    		return false;
    	}
    	
    	for (Entry<K, V> entry : table[index])
    	{
    		if (entry.getKey().equals(key))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }

    // returns boolean if table has the searched for value
    @Override
    public boolean containsValue(Object value) {
    	for (LinkedList<Entry<K, V>> list : table)
    	{
    		for (Entry<K, V> entry : list)
    		{
    			if (entry.getValue().equals(value))
    			{
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    // returns Value if table has the searched for key
    @Override
    public V get(Object key) {
    	int index = key.hashCode() % table.length;
    	
    	if (index < 0)
    	{
    		index += table.length;
    	}
    	
    	if (table[index] == null)
    	{
    		return null;
    	}
    	
    	for (Entry<K, V> nextItem : table[index])
    	{
    		if (nextItem.getKey().equals(key))
    		{
    			return nextItem.getValue();
    		}    		
    	}
    	
    	return null;
    }

    // adds the key and value pair to the table using hashing
    @Override
    public V put(K key, V value) {
    	int index = key.hashCode() % table.length;
    	
    	if (index < 0)
    	{
    		index += table.length;
    	}
    	
    	if (table[index] == null)
    	{
    		table[index] = new LinkedList<>();
    	}
    	
    	for (Entry<K, V> nextItem : table[index])
    	{
    		if (nextItem.getKey().equals(key))
    		{
    			V oldVal = nextItem.getValue();
    			nextItem.setValue(value);
    			return oldVal;
    		}
    	}
    	
    	table[index].addFirst(new Entry<>(key, value));
    	numKeys++;
    	
    	if (numKeys > (LOAD_THRESHOLD * table.length))
    	{
    		rehash();
    	}
    	
    	return null;
    }


    /**
     * Resizes the table to be 2X +1 bigger than previous
     */
    private void rehash() {
    	LinkedList<Entry<K, V>>[] oldTable = table;
    	table = new LinkedList[oldTable.length * 2 + 1];
    	numKeys = 0;
    	
    	for (LinkedList<Entry<K, V>> list : oldTable)
    	{
    		if (list != null)
    		{
    			for (Entry<K, V> entry : list)
    			{
    				put(entry.getKey(), entry.getValue());
    				numKeys++;
    			}
    		}
    	}
    		
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder() ;
        for (int i = 0 ; i < table.length ; i++ ) {
            if (table[i] != null) {
                for (Entry<K, V> nextItem : table[i]) {
                    sb.append(nextItem.toString() + " ") ;
                }
                sb.append(" ");
            }
        }
        return sb.toString() ;

    }

    // remove an entry at the key location
    // return removed value
    @Override
    public V remove(Object key) {
    	int index = key.hashCode() % table.length;
    	if (index < 0)
    	{
    		index += table.length;
    	}
    	
    	if (table[index] == null)
    	{
    		return null;
    	}
    	
    	for (Entry<K, V> entry : table[index])
    	{
    		if (entry.getKey().equals(key))
    		{
    			V value = entry.getValue();
    			table[index].remove(entry);
    			numKeys--;
    			
    			if (table[index].isEmpty())
    			{
    				table[index] = null;
    			}
    			
    			return value;		
    		}
    	}
    	
    	return null;
    }

    // throws UnsupportedOperationException
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException() ;
    }

    // empties the table
    @Override
    public void clear() {
    	for (LinkedList<Entry<K, V>> list : table)
    	{
    		list = null;
    	}
    }

    // returns a view of the keys in set view
    @Override
    public Set<K> keySet() {
    	Set<K> keySet = new HashSet<K>(size());
        for (LinkedList<Entry<K,V>> list : table) 
        {
            for (Entry<K,V> entry : list) 
            {
                if (entry != null) 
                {
                    keySet.add(entry.getKey());
                }
            }
        }
        
        return keySet;
    }

    // throws UnsupportedOperationException
    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException() ;
    }


    // returns a set view of the hash table
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
    	return new EntrySet();
    }

    @Override
    public boolean equals(Object o) {
    	if (o == null || getClass() != o.getClass())
		{
			return false ;
		}
		HashTableChain<K, V> other = (HashTableChain<K, V>) o ;
		return toString().equals(other.toString()) ;

    }

    @Override
    public int hashCode() {
    	int hash = 11;
    	
    	hash = hash * 17;
    	
    	return hash;

    }
}
