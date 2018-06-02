package utils;

public class BuildXML {
	
	public static String createDepartamento(int id, String nombre, String localidad){
		StringBuilder xmlbuilder = new StringBuilder();
		
		xmlbuilder.append("<DEP_ROW>\n");
		xmlbuilder.append("    <DEPT_NO>"+id+"</DEPT_NO>\n");
		xmlbuilder.append("    <DNOMBRE>"+nombre+"</DNOMBRE>\n");
		xmlbuilder.append("    <LOC>"+localidad+"</LOC>\n");
		xmlbuilder.append("</DEP_ROW>");
		
		return xmlbuilder.toString();
	}

}
