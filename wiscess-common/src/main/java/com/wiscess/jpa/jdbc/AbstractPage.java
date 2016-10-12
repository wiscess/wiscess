package com.wiscess.jpa.jdbc;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPage<E> implements IPage<E> {
	protected List<E> pageElements = new ArrayList<E>();
	
	protected int pageSize = 10;

	protected int totalCount = 0;

	protected int currPageNumber = 0;

	protected int firstPageNumber = 1;
	
	protected int lastPageNumber = 1;
	
	protected int previousPageNumber = 1;
	
	protected int nextPageNumber = 1;

	public AbstractPage(int pageNumber, int pageSize) {
		this.pageSize = pageSize;
		this.currPageNumber = pageNumber;
	}

	protected void computePage() {
		if (pageSize < 1) {
			pageSize = 1;
		}
		double lastPage = (double) totalCount / pageSize;
        lastPageNumber = (int) Math.ceil(lastPage);
		if (currPageNumber > lastPageNumber) {
			currPageNumber = lastPageNumber;
		}
		if (currPageNumber < firstPageNumber) {
			currPageNumber = firstPageNumber;
		}
		currPageNumber = (lastPageNumber == firstPageNumber) ? firstPageNumber : currPageNumber;
		nextPageNumber = hasNextPage() ? currPageNumber + 1 : currPageNumber;
		previousPageNumber = hasPreviousPage() ? currPageNumber - 1 : currPageNumber;
		
	}

	public int getFirstPageNumber() {
		return firstPageNumber;
	}

	public int getLastPageNumber() {
		return lastPageNumber;
	}

	public int getNextPageNumber() {
		return nextPageNumber;
	}

	public List<E> getPageElements() {
		return pageElements;
	}
	
	public IPage<E> setPageElements(List<E> pageElements) {
		this.pageElements = pageElements;
		return this;
	}

	public int getPreviousPageNumber() {
		return previousPageNumber;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public boolean hasNextPage() {
		return currPageNumber < lastPageNumber;
	}

	public boolean hasPreviousPage() {
		return currPageNumber > firstPageNumber;
	}

	public boolean isFirstPage() {
		return currPageNumber == firstPageNumber;
	}

	public boolean isLastPage() {
		return currPageNumber >= lastPageNumber;
	}

	public int getCurrPageNumber() {
		return currPageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

    @Override
    public String toString()
    {
        return new StringBuffer()
            .append("Page Object.")
            .append("\nTotal count: ").append(getTotalCount())
            .append("\nTotal page: ").append(getLastPageNumber())
            .append("\nCurrent page: ").append(getCurrPageNumber())
            .append("\nPage Size: ").append(getPageSize())
            .append("\nList size: ").append(getPageElements().size())
            .toString();
    }

}
