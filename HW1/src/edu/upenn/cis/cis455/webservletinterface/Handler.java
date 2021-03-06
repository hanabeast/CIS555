package edu.upenn.cis.cis455.webservletinterface;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Handler extends DefaultHandler {
	private int m_state = 0;
	private String m_servletName;
	public String m_serverName;
	private String m_paramName;
	private int sessionTimeOut;
	public HashMap<String,String> m_servlets = new HashMap<String,String>();
	public HashMap<String,String> m_urlMappings = new HashMap<String,String>();
	public HashMap<String,String> m_contextParams = new HashMap<String,String>();
	public HashMap<String,HashMap<String,String>> m_servletParams = new HashMap<String,HashMap<String,String>>();
	
	public int getSessionTimeout() {
		return sessionTimeOut;
	}
	public void startDocument() throws SAXException {
		super.startDocument();
        System.out.println("start processing xml. ");
    }
	
    public void endDocument() throws SAXException {
    	super.endDocument();
        System.out.println("end processing xml. ");
    }
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.compareTo("servlet-name") == 0) {
			m_state = 1;
		} else if (qName.compareTo("servlet-class") == 0) {
			m_state = 2;
		} else if (qName.compareTo("context-param") == 0) {
			m_state = 3;
		} else if (qName.compareTo("init-param") == 0) {
			m_state = 4;
		} else if (qName.compareTo("param-name") == 0) {		//10-context-param-name		20-servlet-param-name
			m_state = (m_state == 3) ? 10 : 20;
		} else if (qName.compareTo("param-value") == 0) {		//11-context-param-value	21-servlet-param-value
			m_state = (m_state == 10) ? 11 : 21;
		} else if (qName.compareTo("url-pattern") == 0) {
			m_state = 5;
		} else if (qName.compareTo("display-name") == 0) {
			m_state = 6;
		} else if (qName.compareTo("session-timeout") == 0) {
			m_state = 7;
		}
	}
	public void characters(char[] ch, int start, int length) {
		String value = new String(ch, start, length);
		if (m_state == 1) {
			m_servletName = value;
			m_state = 0;
		} else if (m_state == 2) {
			m_servlets.put(m_servletName, value);
			m_state = 0;
		} else if (m_state == 5) {
			m_urlMappings.put(value, m_servletName);
			m_state = 0;
		} else if (m_state == 6) {
			m_serverName = value;
			m_state = 0;
		} else if (m_state == 7) {
			sessionTimeOut = Integer.valueOf(value) * 60;
			m_state = 0;
		} else if (m_state == 10 || m_state == 20) {
			m_paramName = value;
		} else if (m_state == 11) {
			if (m_paramName == null) {
				System.err.println("Context parameter value '" + value + "' without name");
				System.exit(-1);
			}
			m_contextParams.put(m_paramName, value);
			m_paramName = null;
			m_state = 0;
		} else if (m_state == 21) {
			if (m_paramName == null) {
				System.err.println("Servlet parameter value '" + value + "' without name");
				System.exit(-1);
			}
			HashMap<String,String> p = m_servletParams.get(m_servletName);
			if (p == null) {
				p = new HashMap<String,String>();
				m_servletParams.put(m_servletName, p);
			}
			p.put(m_paramName, value);
			m_paramName = null;
			m_state = 0;
		}
	}
}
/*
 * <servlet>
    <servlet-name>controlServlet</servlet-name>
    <servlet-class>com.jenkov.butterfly.ControlServlet</servlet-class>
    
        <init-param>
        <param-name>myParam</param-name>
        <param-value>paramValue</param-value>
        </init-param>
   </servlet>
 * 
 * 
 * 
 */
