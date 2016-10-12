package com.wiscess.jpa.jdbc;

import java.util.List;

@Deprecated
public interface IPage<E> {
	/**
	 * Whether is the first page.
	 * 
	 * @return
	 */
    public boolean isFirstPage();
    
    /**
     * Whether is the last page.
     * 
     * @return
     */
    public boolean isLastPage();
    
    /**
     * Whether has the next page or not.
     * 
     * @return
     */
    public boolean hasNextPage();
    
    /**
     * Whether has the previous page or not.
     * 
     * @return
     */
    public boolean hasPreviousPage();
    
    /**
     * Get current page elements.
     * 
     * @return
     */
    public List<E> getPageElements();
    
    /**
     * Get the total records count.
     * 
     * @return
     */
    public int getTotalCount();
    
    /**
     * Get the first page No.
     * 
     * @return
     */
    public int getFirstPageNumber();
    
    /**
     * Get the last page No.
     * 
     * @return
     */
    public int getLastPageNumber();
    
    /**
     * Get the next page No.
     * 
     * @return
     */
    public int getNextPageNumber();
    
    /**
     * Get the previous page No.
     * 
     * @return
     */
    public int getPreviousPageNumber();
    
    /**
     * Get current page No.
     * 
     * @return
     */
    public int getCurrPageNumber();
    
    /**
     * Get page size.
     * 
     * @return
     */
    public int getPageSize();
}
