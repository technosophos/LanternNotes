package com.technosophos.lantern.commands.search;

import java.util.Map;

import com.technosophos.lantern.util.Scrubby;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.repository.SearchResults;

public class LanternSearchResults extends SearchResults {
	
	public LanternSearchResults(
			String q, 
			String[] names, 
			Map<String,String> args, 
			int maxResults, 
			int offset,
			int total,
			DocumentList dl
	) {		
		super(q, names, args, maxResults, offset, total, dl);
	}
	
	/**
	 * Decorator constructor.
	 * @param sr
	 */
	public LanternSearchResults(SearchResults sr) {
		super(sr.getQuery(), sr.getNames(), sr.getArgs(), sr.getMaxResults(), sr.getOffset(), sr.getTotalMatches(), sr.getDocumentList());
	}
	
	// A bunch of utility methods for velocity, which has wimpy math support:
	
	public int getListLength(){return this.docList.size();}
	
	public int getNumberOfResultPages() {
		if(this.numberOfResults <= this.maxResults) return 1;
		 
		int pageCount = this.numberOfResults / this.maxResults;
		if(this.numberOfResults % this.maxResults > 0) ++pageCount;
		
		return pageCount;
	}
	
	public int getCurrentPageNumber() {
		if(this.offset == 0) return 1;
		
		int currPage = this.offset / this.maxResults;
		if(this.offset % this.maxResults > 0) ++currPage;
		
		return ++currPage;
	}
	
	public int getNextOffset() {
		int nextOffset =  this.offset + this.maxResults;
		// XXX: Should we try to prevent offset from being calculated if no next page
		// exists?
		return nextOffset;
	}
	
	public int getFirstResultNumber() {
		return this.offset + 1;
	}
	
	public int getLastResultNumber() {
		int t = this.offset + this.maxResults;
		if(t > this.numberOfResults) t = this.numberOfResults;
		return t;
	}
	
	public String getNextQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("q=").append(Scrubby.encodeHTMLChars(this.q, false));
		int nextOffset = this.getNextOffset();
		if(nextOffset < this.numberOfResults)
			sb.append("&amp;o=").append(nextOffset);
		return sb.toString();
	}
	
	public String getQueryStringForPage(int pageNum) {
		pageNum = pageNum -1; // convert to zero index
		StringBuilder sb = new StringBuilder();
		sb.append("q=").append(Scrubby.encodeHTMLChars(this.q, false));
		int nextOffset = pageNum * this.getMaxResults();
		if(nextOffset < this.numberOfResults)
			sb.append("&amp;o=").append(nextOffset);
		return sb.toString();
	}
	
	public String getPreviousQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("q=").append(Scrubby.encodeHTMLChars(this.q, false));
		if(this.offset > this.maxResults) 
			sb.append("&amp;o=").append(this.offset - this.maxResults);
		else if( this.offset > 0)
			sb.append("&amp;o=0");
		return sb.toString();
	}
	
	/**
	 * Get the page window.
	 * This returns an array of pages that are near the current page. This 
	 * can be used to generate a limited list of close pages: <i>4 5 <b>6</b> 7 8</i>.
	 * @param pad
	 * @return
	 */
	public int[] getPageWindow(int pad) {
		int currPage = this.getCurrentPageNumber();
		int lastPage = this.getNumberOfResultPages();
		//int ordPage = lastPage + 1;
		int halfPad = pad / 2;
		int odd = pad % 2;
		
		// If there are fewer pages than pad, return the whole array
		if(lastPage <= pad) return this.range(1,lastPage);
		
		// Find end first:
		int end = currPage + halfPad + odd;
		if (end > lastPage) end = lastPage;
		
		//Start:
		int start = end - pad;
		if(start < 1) {
			start = 1;
			end = start + pad;
			return this.range(1, pad);
		} else {
			return this.range(start + 1, end - start);
		}
		
		// Distance from start to end
		// FIXME: This isn't right -- just a quick stopgap for fixing the above.
		//int dist = end - start + 1;
		
		//System.err.format("### Current: %d, Last Page: %d Start: %d, End: %d, Distance: %d\n", 
		//		currPage, lastPage, start, end, dist);
		
		//return this.range(start, dist);
	}
	
	private int[] range(int start, int numberOfItems) {
		int ii = start;
		int [] s = new int[numberOfItems];
		for(int i = 0; i < numberOfItems; ++i) s[i] = ii++;
		return s;
	}
	
	/**
	 * Return a PageWindow object instead of an int array.
	 * @param pad
	 * @return
	 */
	public PageWindow getPageWindowObj(int pad) {
		int [] i = this.getPageWindow(pad);
		return new PageWindow(
			i,
			i[0],
			i[i.length - 1]
		);
	}
	
	/**
	 * Helper class for page window.
	 * It does the obvious, but Velocity needs it b/c of poor array support.
	 * @author mbutcher
	 *
	 */
	public class PageWindow {
		int firstPage, lastPage;
		int[] window;
		PageWindow(int[] window, int firstPage, int lastPage) {
			this.firstPage = firstPage; this.lastPage = lastPage; this.window = window;
		}
		public int getFirstPage() { return this.firstPage; }
		public int getLastPage() { return this.lastPage; }
		public int[] getWindow() { return this.window; }
	}
}
