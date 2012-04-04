package fr.utc.assos.payutc.soap;



public class IdentityResult extends SoapResult {
	public String prenom, nom, surnom, id_photo;
	public int id_user, solde;
	
	public IdentityResult(String wsdl_response){
		super(wsdl_response);
	}

	@Override
	void processValues(String[][] values) {
		id_user = Integer.parseInt(values[0][0]);
		prenom = values[0][1];
		nom = values[0][2];
		surnom = values[0][3];
		id_photo = values[0][4];
		solde = Integer.parseInt(values[0][5]);
	}
}
