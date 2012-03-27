package fr.utc.assos.payutc.soap;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.LabeledCSVParser;

public class Proposition {
	public int id, cost;
	public String name, type, img_id;
	
	public Proposition(int _id, String _name, String _type, String _img_id, int _cost) {
		id = _id;
		name = _name;
		type = _type;
		img_id = _img_id;
		cost = _cost;
	}
	
	
	
	public static ArrayList<Proposition> parse_response(String wsdl_response) throws IOException {
		
		ArrayList<Proposition> propositions = new ArrayList<Proposition>();
		
		LabeledCSVParser lcsvp = new LabeledCSVParser(
			new CSVParser(
				new StringReader(
					"\"id\",\"name\",\"type\",\"img_id\",\"cost\";\n" +
					wsdl_response
				)
			)
		);
		
		while(lcsvp.getLine() != null) {
			propositions.add(
				new Proposition(
					Integer.parseInt(lcsvp.getValueByLabel("id")),
					lcsvp.getValueByLabel("name"),
					lcsvp.getValueByLabel("type"),
					lcsvp.getValueByLabel("img_id"),
					Integer.parseInt(lcsvp.getValueByLabel("cost"))
				)
			);
		}
		
		return propositions;
	}
}
