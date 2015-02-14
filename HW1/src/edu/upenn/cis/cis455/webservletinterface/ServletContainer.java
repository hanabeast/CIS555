package edu.upenn.cis.cis455.webservletinterface;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ServletContainer {
	private HashMap<String, HttpServlet> servlets;
	private HashMap<String, String> urlPatterns;
	private FakeContext fContext;
	
	public HashMap<String, HttpServlet> getServlets() {
		return servlets;
	}

	public HashMap<String, String> getUrlPatterns() {
		return urlPatterns;
	}

	public FakeContext getfContext() {
		return fContext;
	}

	public ServletContainer(String webdotxml) throws Exception {
		Handler handler = parseWebdotxml(webdotxml);
		fContext = createContext(handler);
		servlets = createServlets(handler, fContext);
		urlPatterns = getUrlPatterns(handler);
	}
	
	private Handler parseWebdotxml(String webdotxml) throws Exception {
		Handler h = new Handler();
		File file = new File(webdotxml);
		if (file.exists() == false) {
			System.err.println("error: cannot find " + file.getPath());
			System.exit(-1);
		}
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, h);
		return h;
	} 
	
	//set parameters and attributes
	private FakeContext createContext(Handler h) {
		FakeContext fc = new FakeContext();
		for (String param : h.m_contextParams.keySet()) {
			fc.setInitParam(param, h.m_contextParams.get(param));
		} 
		// attributes ???
		return fc;
	}
	
	private HashMap<String, String> getUrlPatterns(Handler handler) {
		HashMap<String, String> urlPatterns = new HashMap<String, String>();
		for (String urlP : handler.m_urlMappings.keySet()) {
			urlPatterns.put(urlP, handler.m_urlMappings.get(urlP));
		}
		return urlPatterns;
	}
	
	private HashMap<String,HttpServlet> createServlets(Handler h, FakeContext fc) throws Exception {
		HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
		for (String servletName : h.m_servlets.keySet()) {
			FakeConfig config = new FakeConfig(servletName, fc);
			String className = h.m_servlets.get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String,String> servletParams = h.m_servletParams.get(servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(servletName, servlet);
		}
		return servlets;
	}
}