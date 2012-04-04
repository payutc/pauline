package fr.utc.assos.payutc.soap;

import java.util.ArrayList;

import fr.utc.assos.payutc.Item;

public class GetPropositionResult extends SoapResult {
	
	private ArrayList<Item> mItems;
	
	public GetPropositionResult(String wsdl_response) {
		super(wsdl_response);
	}
	
	@Override
	protected void processValues(String[][] values) {
		mItems = new ArrayList<Item>();
		for (String[] line : values) {
			mItems.add(
				new Item(
					Integer.parseInt(line[0]),
					line[1],
					line[2],
					Integer.parseInt(line[3]),
					Integer.parseInt(line[4])
				)
			);
		}
	}
	
	public ArrayList<Item> getItems() {
		return mItems;
	}
}
