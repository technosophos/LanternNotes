package com.technosophos.lantern.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
//import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Arrays;
import java.lang.Iterable;

import com.technosophos.rhizome.util.Timestamp;
import com.technosophos.lantern.util.Scrubby;

/**
 * Utilities for use in Velocity templates.
 * @author mbutcher
 *
 */
public class TemplateTools {
	private DateFormat inDF;
	private SimpleDateFormat outDF;
	public final static String DEFAULT_FORMAT = "h:mm a 'on' MMM. d, yyyy";
	
	public final static String CSS_CLASS_TOP_TAG = "top-tag";
	public final static String CSS_CLASS_MIDDLE_TAG = "middle-tag";
	public final static String CSS_CLASS_LOW_TAG = "low-tag";
	
	public TemplateTools() {
		// Get the timestamp formatter used by Rhizome.
		this.inDF = Timestamp.getDateFormatter();
		try {
			this.outDF = (SimpleDateFormat)Timestamp.getDateFormatter();
		} catch (RuntimeException e) {
			// This can happen if Locale is weird.
			this.outDF = new SimpleDateFormat();
		}
		this.outDF.applyPattern(DEFAULT_FORMAT);
	}
	
	/**
	 * Format a date for display.
	 * @param date Date string from Rhizome.
	 * @return Formatted date string
	 */
	public String formatDate(String date) {
		if(date == null || date.length() == 0)return "";
		try {
			Date d = this.inDF.parse(date);
			//this.outDF.applyPattern(DEFAULT_FORMAT);
			return this.outDF.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
	
	/**
	 * Format a date for display.
	 * @param date Date string from Rhizome.
	 * @param format String format for this date
	 * @return Formatted date string
	 * @see java.text.SimpleDateFormat
	 */
	public String formatDate(String date, String format) {
		try {
			Date d = this.inDF.parse(date);
			this.outDF.applyPattern(format);
			return this.outDF.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
	
	public String formatTags(String[] tags, String uri) {
		StringBuilder sb = new StringBuilder();
		boolean cm = false;
		for(String tag: tags) {
			if(cm) sb.append(", ");
			sb.append("<a href=\"").append(uri).append(Scrubby.URLEncode(tag)).append("\">")
				.append(tag).append("</a>");
			cm = true;
		}
		return sb.toString();
	}
	
	/*
	public String formatTags(java.util.List<String> tags, String uri) {
		StringBuilder sb = new StringBuilder();
		boolean cm = false;
		for(String tag: tags) {
			if(cm) sb.append(", ");
			sb.append("<a href=\"").append(uri).append(Scrubby.URLEncode(tag)).append("\">")
				.append(tag).append("</a>");
			cm = true;
		}
		return sb.toString();
	}
	*/
	/**
	 * Format a list of tags.
	 * @param tags An Interable containing the tags that should be formatted.
	 * @param uri The URI that the tags should link to. The tag will be appended to the string.
	 * @param separator The string used to separate the tags in display.
	 */
	public String formatTags(Iterable<String> tags, String uri, String separator) {
		//System.out.println("TemplateTools: Formatting Iterable as tag string.");
		StringBuilder sb = new StringBuilder();
		boolean cm = false;
		for(String tag: tags) {
			//System.out.print("Ping;");
			if(cm) sb.append(separator); else cm = true;
			sb.append("<a class=\"tag\" href=\"").append(uri).append(Scrubby.URLEncode(tag)).append("\">")
				.append(tag).append("</a>");
		}
		//System.out.println("TemplateTools:" + sb.toString());
		return sb.toString();
	}
	public String formatTags(Iterable<String> tags, String uri) {
		return this.formatTags(tags, uri, ", ");
	}
	public String tagCloud(Map<String, Integer> tags, String uri) {
		StringBuilder sb = new StringBuilder();
		String format = "<a href=\""+ uri +"%s\" class=\"tag %s\">%s</a>";
		int highest = 1; int lowest = 0;
		/*String[] classes = {
			CSS_CLASS_LOW_TAG,
			CSS_CLASS_MIDDLE_TAG,
			CSS_CLASS_TOP_TAG	
		};*/
		
		String[] keys = tags.keySet().toArray(new String[0]);
		for(String k: keys) {
			Integer s = tags.get(k);
			if(s > highest) highest = s;
			else if(s == 0 ) s = lowest; // Assuming no tag can have 0 hits.
			else if(s < lowest) s = lowest;
		}
		int median = (highest - lowest) / 2;
		int upperq = (highest - median) / 2;
		int lowerq = (median - lowest) / 2;
		
		Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
		String c;
		Integer val;
		boolean punc = false;
		final String sep = ", ";
		for(String k: keys) {
			val = tags.get(k);
			c = val < lowerq ? CSS_CLASS_LOW_TAG : 
					val <= upperq ? CSS_CLASS_MIDDLE_TAG : CSS_CLASS_TOP_TAG;
			if(punc) sb.append(sep); else punc = true;
			sb.append(String.format(format, k, c, k));
		}
		
		return sb.toString();
	}
	
	public String implode(String sep, List<String> values) {
		boolean punc = false;
		if(values == null) return "";
		if(sep == null) sep=", ";
		StringBuilder sb = new StringBuilder();
		for(String s: values) {
			// Using ternary generates spurious exception
			if(punc) sb.append(sep); else punc = true;
			sb.append(s);
		}
		return sb.toString();
	}
	
	public Map<String, String> parseLabeledURI(String[] items) {
		return this.parseLabeledURI(Arrays.asList(items));
	}
	/**
	 * Take a labeled URI string (that's a URI with a label at the end) and split it.
	 * This returns a map where URI is the key, and label is the value. If no label is found,
	 * the URI is also set as the label.
	 * @param items List of labeled URIs
	 * @return Key is URI and value is label.
	 */
	public Map<String, String> parseLabeledURI(List<String> items) {
		Map<String, String> map = new java.util.HashMap<String, String>();
		//System.out.println("-----------------------------> parsing label");
		if(items == null) return map;
		String[] parts;
		String val;
		for(String item: items) {
			if(item != null) {
				parts = item.split("\\s",2);
				val = (parts.length > 1) ? parts[1] : parts[0]; 
				map.put(parts[0], val);
			}
		}
		return map;
	}
	
	public List<SourceIDInfo> parseIdentifier(List<String> ids) {
		List<SourceIDInfo> list = new java.util.ArrayList<SourceIDInfo>();
		if(ids == null || ids.size() == 0) return list;
		String[] parts;
		for(String item: ids) {
			if(item != null) {
				parts = item.split(":",2);
				if(parts.length == 0) {
					list.add(new SourceIDInfo("ID", parts[0]));
				} else {
					list.add(new SourceIDInfo(parts[0], parts[1]));
				}
			}
		}
		return list;
	}
	
	/**
	 * Given an identifier code (like lcc, lccn, ddc, isbn), returns a name (Library of Congress Number)
	 * @param idCode
	 * @return
	 * @see com.technosophos.lantern.xml.mods.MODS For more identifiers not used here.
	 */
	public String identifierName(String idCode) {
		if("lccn".equals(idCode)) return "Library of Congress Control Number";
		else if("lcc".equals(idCode)) return "Library of Congress Number";
		else if("ddc".equals(idCode)) return "Dewey Decimal Classifier";
		else if("isbn".equals(idCode)) return "ISBN (Book Number)";
		else if("issn".equals(idCode)) return "ISSN (Serial/Periodical Number)";
		else if("uri".equals(idCode)) return "URL";
		else if("dc".equals(idCode)) return "Dublin Core ID";
		else return idCode.toUpperCase();
	}
	
	/**
	 * Turn an array into a list, which is easier to manipulate in Velocity.
	 * Helper for templates. Since {@link Arrays} is not in scope for velocity, this
	 * helper simply uses the toList() function of {@link Arrays} to convert an array to a
	 * {@link List}.
	 * @param array Array to convert
	 * @return List containing array.
	 */
	public List<Object> toList(Object[] array){
		return Arrays.asList(array);
	}
	
	/**
	 * Check to see if a given object is not empty.
	 * This can check the following types:
	 * <ul>
	 * <li>Collection (true if not null and size > 0)</li>
	 * <li>Map (true if not null and size > 0)</li>
	 * <li>Object[] (True if not null and length > 0)</li>
	 * <li>String (true if not null and length > 0)</li>
	 * <li>Boolean (true if true)</li>
	 * <li>Number (true if intval is > 0)</li>
	 * </ul>
	 * <p>For any other object, if it's not null, it is not empty.</p>
	 * @param o
	 * @return
	 */
	public boolean notEmpty(Object o) {
		if (o == null) return false;
		if (o instanceof Iterable) {
			// TODO: rework this with generics?
			Iterable<Object> c = (Iterable<Object>)o;
			//System.err.format("\nIterable being tested: %s.\n", o.toString());
			java.util.Iterator<Object> i = c.iterator();
			if(!i.hasNext()) return false;
			Object oo = i.next();
			if(oo.toString().length() > 0) return true;
			return i.hasNext();
		//} else if (o instanceof Collection) {
		//	Collection<Object> c = (Collection<Object>)o;
		//	return c.size() > 0;
		} else if( o instanceof Map) {
			Map<Object, Object> c = (Map<Object,Object>)o;
			return c.size() > 0;
		} else if( o instanceof Object[]) {
			Object[] c = (Object[])o;
			//System.err.println("\nObject array being tested.\n");
			if(c.length == 1) {
				return c[0].toString().length() > 0;
			} else if (c.length > 1) {
				return true;
			}
			return false;
		//} else if (o instanceof String[]) {
		//	Object[] c = (Object[])o;
		//	System.err.println("\nString array being tested.\n");
		//	return c.length > 0;
		} else if( o instanceof String) {
			return ((String)o).length() > 0;
		} else if( o instanceof Boolean) {
			return (Boolean)o;
		} else if( o instanceof Number) {
			return ((Number)o).intValue() > 0;
		}
		return true; // Default case: assume that if not null, not empty
	}
	/**
	 * Shorthand alias for {@link notEmpty(Object)}
	 * @param o Object to test
	 * @return true if the object is not empty, false otherwise.
	 */
	public boolean ne(Object o){ return this.notEmpty(o); }
	
	/**
	 * Represents a label/id pair, like isbn:12345678909876.
	 * @author mbutcher
	 *
	 */
	public class SourceIDInfo {
		String l, i;
		public SourceIDInfo(String label, String id) {
			this.l = label;
			this.i = id;
		}
		public String getLabelCode() {return this.l; }
		public String getLabelString() { return identifierName(this.l); }
		public String getID(){ return this.i; }
		public String toString(){ return this.l + ":" + this.i; }
	}
	
	public String formatNameFirstLast(String str) {
		String name = this.dropDateFromName(str);
		String[] flnames = name.split(",",2);
		if(flnames.length == 2) {
			StringBuilder sb = new StringBuilder();
			return sb.append(flnames[1].trim()).append(' ').append(flnames[0].trim()).toString();
		} else return name;
	}
	
	public String dropDateFromName(String name) {
		// FIXME: This should check to make sure we are really dropping date.
		
		char[] chars = name.toCharArray();
		boolean hasDash = false;
		int digitCount = 0;
		for(char c: chars) {
			// If it has a dash and some numbers, it is probably a date.
			if( c == '-' ) hasDash = true;
			else if (Character.isDigit(c)) ++digitCount;
		}
		if(hasDash && digitCount > 0) {
			int lastSpace = name.lastIndexOf(' ');
			return lastSpace > 0 ? name.substring(0, lastSpace): name;
		}
		return name;
	}
	
	public String formatNames(List<String> names) {
		if(names == null || names.size() == 0)return "";
		StringBuilder sb = new StringBuilder();
		boolean del = false;
		for(String name: names) {
			if(del) sb.append(", "); else del = true;
			sb.append(this.formatNameFirstLast(name));
		}
		return sb.toString();
	}
	
	/**
	 * Escape for HTML/XML display.
	 * @param str
	 * @return
	 */
	public String escape(String str) {
		return Scrubby.encodeHTMLChars(str, false);
	}
	
	public String e(String str) {
		return this.escape(str);
	}
	
	public Scrubby scrubby() { return new Scrubby(); }

}
