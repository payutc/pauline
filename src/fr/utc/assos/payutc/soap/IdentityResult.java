package fr.utc.assos.payutc.soap;


import java.io.IOException;
import java.io.StringReader;

import com.Ostermiller.util.CSVParser;

public class IdentityResult {
	public String prenom, nom, surnom, id_photo;
	public int id_user, solde;
	
	public IdentityResult(int _id_user, 
			String _prenom, String _nom, String _surnom, 
			String _id_photo, 
			int _solde) {
		id_user = _id_user;
		prenom = _prenom;
		nom = _nom;
		surnom = _surnom;
		id_photo = _id_photo;
		solde = _solde;
	}
	
	public IdentityResult(String wsdl_response) throws IOException {
		CSVParser lcsvp = new CSVParser(
			new StringReader(
				wsdl_response + "\n"
			)
		);
		String[] line = lcsvp.getLine();
		id_user = Integer.parseInt(line[0]);
		prenom = line[1];
		nom = line[2];
		surnom = line[3];
		id_photo = line[4];
		solde = Integer.parseInt(line[5]);
	}
}
