package karbonator.util;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public class Triplet<L, C, R> {
    public Triplet(Triplet<L, C, R> src) {
        this(src.left, src.center, src.right);
    }
    
    public Triplet(L left, C center, R right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((null == left) ? 0 : left.hashCode());
        result = prime * result + ((null == center) ? 0 : center.hashCode());
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
        Triplet<L, C, R> other = (Triplet<L, C, R>)o;
        
        if(null == left) {
            if(null != other.left) {
                return false;
            }
        }
        else if(!left.equals(other.left)) {
            return false;
        }
        
        if(null == center) {
            if(null != other.center) {
                return false;
            }
        }
        else if(!center.equals(other.center)) {
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
        return String.format("[%s,%s,%s]", left, center, right);
    }
    
    public final L left;
    
    public final C center;
    
    public final R right;
}
