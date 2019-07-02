package karbonator.util;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public class Pair<L, R> {
    public Pair(Pair <L, R> src) {
        this(src.left, src.right);
    }
    
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((null == left) ? 0 : left.hashCode());
        result = prime * result + ((null == right) ? 0 : right.hashCode());
        
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        
        if(null == o || getClass() != o.getClass()) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Pair<L, R> other = (Pair<L, R>)o;
        if(null == left) {
            if(null != other.left) {
                return false;
            }
        }
        else if(!left.equals(other.left)) {
            return false;
        }
        
        if(null == right) {
            if(null != other.right) {
                return false;
            }
        }
        else if(!right.equals(other.right)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s,%s]", left, right);
    }
    
    public final L left;
    
    public final R right;
}
