package utils;

public class ExtractXML {
	
	private String XML;
	
	public ExtractXML (String xml){
		XML = xml;
	}
	
	public String getField(String field){
		try{
			int posini = XML.indexOf("<"+field+">") + field.length() + 2;
			int posfin = XML.indexOf("</"+field+">");
			return XML.substring(posini, posfin);
		}catch (java.lang.StringIndexOutOfBoundsException e){
			return null;
		}
	}
}
